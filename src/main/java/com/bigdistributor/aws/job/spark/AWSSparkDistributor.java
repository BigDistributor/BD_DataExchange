package com.bigdistributor.aws.job.spark;

import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.aws.data.AWSN5Supplier;
import com.bigdistributor.aws.data.CredentialSupplier;
import com.bigdistributor.aws.dataexchange.aws.s3.S3Utils;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.job.utils.LogAccumulator;
import com.bigdistributor.aws.job.utils.TaskLog;
import com.bigdistributor.aws.job.utils.TimeLabel;
import com.bigdistributor.aws.spimloader.SpimLoadSupplier;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.biglogger.adapters.LoggerManager;
import com.bigdistributor.core.app.ApplicationMode;
import com.bigdistributor.core.app.BigDistributorApp;
import com.bigdistributor.core.app.BigDistributorMainApp;
import com.bigdistributor.core.metadata.MetadataGenerator;
import com.bigdistributor.core.task.BlockTask;
import com.bigdistributor.core.task.JobID;
import com.bigdistributor.core.task.items.Metadata;
import com.bigdistributor.core.task.items.SerializableParams;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import net.preibisch.mvrecon.fiji.spimdata.boundingbox.BoundingBox;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.janelia.saalfeldlab.n5.DataType;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

// T: DataType exmpl FloatType
// K: Task Param
@BigDistributorApp(mode = ApplicationMode.ExecutionNode)
public class AWSSparkDistributor<T extends NativeType<T>, K extends SerializableParams> extends BigDistributorMainApp implements Callable<Void> {
    @Option(names = {"-o", "--output"}, required = true, description = "The 3 uri of the output Data, e.g. s3://bucket/output.n5/ ")
    String output;

    @Option(names = {"-i", "--input"}, required = true, description = "The s3 uri of the input Data, e.g. s3://bucket/dataset.xml ")
    String input;

    @Option(names = {"-po", "--path"}, required = false, description = "The path of the input Data inside bucket, e.g. volumes/raw")
    String dataset = "/volumes/raw";

    @Option(names = {"-pk", "--publicKey"}, required = false, description = "Credential public key")
    String credPublicKey;

    @Option(names = {"-pp", "--privateKey"}, required = false, description = "Credential private key")
    String credPrivateKey;

    @Option(names = {"-id", "--jobid"}, required = true, description = "The jod Id")
    String jobId;

    @Option(names = {"-m", "--metadata"}, required = false, description = "The s3 uri of the MetaData file, e.g. s3://bucket/metadata.json ")
    String metadataPath;

    @Option(names = {"-p", "--param"}, required = false, description = "The se uri of the params file, e.g. s3://bucket/params.json ")
    String paramPath;

    @Option(names = {"-r", "--region"}, required = false, description = "The region where you stored your data, e.g.  eu-central-1 / us-east-1")
    String region ="eu-central-1";

    @Option(names = "--blockSize", required = false, description = "Size of output blocks, e.g. 128,128,64 or 512,512 (default: as listed before)")
    private String blockSizeString = null;
    private int[] blockSize;
    private static int[] defaultBlockSize2d = new int[]{512, 512};
    private static int[] defaultBlockSize3d = new int[]{128, 128, 64};


    private static final Log logger = Log.getLogger(AWSSparkDistributor.class.getSimpleName());

    private Class<BlockTask> mainTask;

    private Metadata md;

    public AWSSparkDistributor(Class<BlockTask> task) {
        this.mainTask = task;
    }

    @Override
    public Void call() throws Exception {

        SparkConf sparkConf = new SparkConf().setAppName(JobID.get()).set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        logger.info("Starting Main app ..");
        final CredentialSupplier credSupplier = new CredentialSupplier(credPublicKey, credPrivateKey,region);
        AmazonS3 s3 = credSupplier.getS3();
//        initCredentials();

        File logFile = new File("log_" + jobId + ".txt");
        File sparkLog = new File("log_spark_" + jobId + ".txt");

        LogAccumulator accumulator = new LogAccumulator(logFile.getAbsolutePath());
        TaskLog mainLog = new TaskLog(-1);
        mainLog.setStatus(TimeLabel.TaskStarted);
        JobID.set(jobId);

        logger.info("App Id: " + jobId);

        LoggerManager.initLoggers();
//        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, bucketName, path);
        SpimLoadSupplier inputSupplier = new SpimLoadSupplier(credSupplier, input);
        if (metadataPath != null) {
            md = Metadata.fromJsonString(S3Utils.get(s3, metadataPath));
            logger.info("Got metadata !");
            if (md == null) {
                logger.error("Error metadata file !");
                return null;
            }
        } else {
            if (this.blockSizeString == null) {
                this.blockSize = defaultBlockSize3d.clone();
            } else {
                this.blockSize = new int[defaultBlockSize3d.length];
                parseCSIntArray(blockSizeString, blockSize);
            }
            md = new MetadataGenerator(inputSupplier.getLoader().getSpimdata()).generate().getMetadata();
        }

        Interval interval = md.getBb();
        long[] blocksizes = md.getBlocksize();

        logger.info(JobID.get() + " started!");
        SerializableParams<K> params = null;
        if (paramPath != null)
            try {
                params = new SerializableParams<K>().fromJsonString(S3Utils.get(s3, paramPath));
            } catch (Exception e) {
                logger.error("Invalid params: " + e.toString());
            }



        mainLog.setStatus(TimeLabel.DataLoaded);
        AWSN5Supplier n5OutputSupplier = new AWSN5Supplier(output, credSupplier);

        createOutput(n5OutputSupplier.getWriter(), interval, blocksizes);

        mainLog.setStatus(TimeLabel.OutputCreated);
        accumulator.append(mainLog.toString());
        final SparkContext sc = new SparkContext(sparkConf);
        sc.addSparkListener(new CustomSparkListener(sparkLog));
        final JavaSparkContext sparkContext = new JavaSparkContext(sc);

        final SerializableParams<K> finalParams = params;

        final String outputUri = output;
        final String inputUri = input;
        final String outputDataset = dataset;
        final BlockTask application = mainTask.newInstance();
        sparkContext.parallelize(md.getBlocksInfo(), md.getTotalBlocks()).foreach(binfo -> {

            int blockID = binfo.getBlockId();
            System.out.println("Block "+blockID + " started ..");
//            TaskLog log = new TaskLog(blockID);
//            log.setStatus(TimeLabel.TaskStarted);
//            Log logger = Log.getLogger("Block: " + blockID);
//            logger.blockStarted(blockID, " start processing..");

            SpimData2 sp =  new SpimLoadSupplier(credSupplier, inputUri).getLoader().getSpimdata();
            BoundingBox bb = new BoundingBox(Util.long2int(binfo.getMin()), Util.long2int(binfo.getMax()));

//            SpimLoadSupplier inputSupplier = new SpimLoadSupplier(credSupplier, input);

            RandomAccessibleInterval result = application.blockTask(sp, finalParams, bb);

//            log.setStatus(TimeLabel.FinishProcess);
//            logger.blockLog(blockID, " Got processed image");
            AWSN5Supplier instanceOutputSupplier = new AWSN5Supplier(outputUri, credSupplier);

            N5Writer writer = instanceOutputSupplier.getWriter();
            N5Utils.saveBlock(result , writer, outputDataset, binfo.getGridOffset());
//            log.setStatus(TimeLabel.DataSaved);
//            logger.blockDone(blockID, " Task done.");
//            log.setStatus(TimeLabel.FinishProcess);

            System.out.println("Block "+blockID + " done !");
        });

        mainLog.setStatus(TimeLabel.TaskDone);
        accumulator.append(mainLog.toString());
        S3BucketInstance.get().uploadFile(logFile);
        return null;
    }

    private void createOutput(N5Writer n5, Interval interval, long[] blocksizes) throws IOException {
        long[] dims = dimensionsAsLongArray(interval);
        int[] blockSize = Arrays.stream(blocksizes).mapToInt(i -> (int) i).toArray();
        final DatasetAttributes attributes = new DatasetAttributes(
                dims,
                blockSize,
                DataType.FLOAT32,
                new GzipCompression());
        n5.createDataset(dataset, attributes);
    }

    long[] dimensionsAsLongArray(Interval bb) {
        long[] dims = new long[bb.numDimensions()];
        bb.dimensions(dims);
        return dims;
    }

    protected static final boolean parseCSIntArray(final String csv, final int[] array) {

        final String[] stringValues = csv.split(",");
        if (stringValues.length != array.length)
            return false;
        try {
            for (int i = 0; i < array.length; ++i)
                array[i] = Integer.parseInt(stringValues[i]);
        } catch (final NumberFormatException e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }
}

