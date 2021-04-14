package com.bigdistributor.aws.job.spark;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.read.AWSReader;
import com.bigdistributor.aws.job.utils.LogAccumulator;
import com.bigdistributor.aws.job.utils.TaskLog;
import com.bigdistributor.aws.job.utils.TimeLabel;
import com.bigdistributor.aws.spimloader.SpimLoadSupplier;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.biglogger.adapters.LoggerManager;
import com.bigdistributor.core.app.ApplicationMode;
import com.bigdistributor.core.app.BigDistributorApp;
import com.bigdistributor.core.app.BigDistributorMainApp;
import com.bigdistributor.core.task.BlockTask;
import com.bigdistributor.core.task.JobID;
import com.bigdistributor.core.task.items.Metadata;
import com.bigdistributor.core.task.items.SerializableParams;
import com.bigdistributor.io.mvrecon.SpimHelpers;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
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
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

// T: DataType exmpl FloatType
// K: Task Param
@BigDistributorApp(mode = ApplicationMode.ExecutionNode)
public class AWSSparkDistributor<T extends NativeType<T>, K extends SerializableParams> extends BigDistributorMainApp implements Callable<Void> {
    @Option(names = {"-o", "--output"}, required = true, description = "The path of the output Data")
    String output;

    @Option(names = {"-i", "--input"}, required = true, description = "The path of the input Data inside bucket")
    String input;

    @Option(names = {"-p", "--path"}, required = true, description = "The path of the input Data inside bucket")
    String path;

    @Option(names = {"-b", "--bucket"}, required = true, description = "The name of bucket")
    String bucketName;

    @Option(names = {"-pk", "--publicKey"}, required = false, description = "Credential public key")
    String credPublicKey;

    @Option(names = {"-pp", "--privateKey"}, required = false, description = "Credential private key")
    String credPrivateKey;

    @Option(names = {"-id", "--jobid"}, required = true, description = "The jod Id")
    String jobId;

    @Option(names = {"-m", "--metadata"}, required = true, description = "The path of the MetaData file")
    String metadataPath;

    @Option(names = {"-p", "--param"}, required = false, description = "The path of the params file")
    String paramPath;


    private static final Log logger = Log.getLogger(AWSSparkDistributor.class.getSimpleName());

    private Class<BlockTask> mainTask;

    private Metadata md;
    private static String dataset = "/volumes/raw";

    public AWSSparkDistributor(Class<BlockTask> task) {
        this.mainTask = task;
    }

    @Override
    public Void call() throws Exception {
        AWSCredentialInstance.initWithKey(credPublicKey,credPrivateKey);
        File logFile = new File("log_" + jobId + ".txt");
        File sparkLog = new File("log_spark_" + jobId + ".txt");
        logger.info("Starting Main app ..");
        LogAccumulator accumulator = new LogAccumulator(logFile.getAbsolutePath());
        TaskLog mainLog = new TaskLog(-1);
        mainLog.setStatus(TimeLabel.TaskStarted);
        JobID.set(jobId);

        initCredentials();

        logger.info("App Id: " + jobId);

        LoggerManager.initLoggers();
        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, bucketName,path);

        md = Metadata.fromJsonString(new AWSReader(S3BucketInstance.get(), "", metadataPath).get());
        logger.info("Got metadata !");
        if (md == null) {
            logger.error("Error metadata file !");
            return null;
        }

        logger.info(JobID.get() + " started!");
        SerializableParams<K> params = null;
        if (paramPath != null)
            try {
                params = new SerializableParams<K>().fromJsonString(new AWSReader(S3BucketInstance.get(), "", paramPath).get());
            } catch (Exception e) {
                logger.error("Invalid params: " + e.toString());
            }
        BlockTask application = mainTask.newInstance();
        SpimLoadSupplier supplier = new SpimLoadSupplier(credPublicKey, credPrivateKey, bucketName, input, output);

        SparkConf sparkConf = new SparkConf().setAppName(JobID.get()).setMaster("local").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");


        mainLog.setStatus(TimeLabel.DataLoaded);
        N5Writer n5 = new N5AmazonS3Writer(S3BucketInstance.get().getS3(), bucketName, output);

        createOutput(n5, md);

        mainLog.setStatus(TimeLabel.OutputCreated);
        accumulator.append(mainLog.toString());
        final SparkContext sc = new SparkContext(sparkConf);
        sc.addSparkListener(new CustomSparkListener(sparkLog));
        final JavaSparkContext sparkContext = new JavaSparkContext(sc);

        SerializableParams<K> finalParams = params;

        sparkContext.parallelize(md.getBlocksInfo(), md.getBlocksInfo().size()).foreach(binfo -> {

            int blockID = binfo.getBlockId();

            TaskLog log = new TaskLog(blockID);
            log.setStatus(TimeLabel.TaskStarted);
            Log logger = Log.getLogger("Block: " + blockID);
            logger.blockStarted(blockID, " start processing..");

            SpimData2 sp = supplier.getLoader().getSpimdata();
            BoundingBox bb = SpimHelpers.getBb(binfo);

            log.setStatus(TimeLabel.DataLoaded);

            RandomAccessibleInterval result = application.blockTask(sp, finalParams, bb);

            log.setStatus(TimeLabel.FinishProcess);
            logger.blockLog(blockID, " Got processed image");
            N5Writer writer = supplier.getN5Output();
            N5Utils.saveBlock(result, writer, dataset, binfo.getGridOffset());
            log.setStatus(TimeLabel.DataSaved);
            logger.blockDone(blockID, " Task done.");
            log.setStatus(TimeLabel.FinishProcess);
            accumulator.append(log.toString());
        });

        mainLog.setStatus(TimeLabel.TaskDone);
        accumulator.append(mainLog.toString());
        S3BucketInstance.get().uploadFile(logFile);
        return null;
    }

    private void initCredentials() {
        if (credPrivateKey != null && credPublicKey != null) {
            logger.info("Init credentials with keys ..");
            AWSCredentialInstance.initWithKey(credPublicKey, credPrivateKey);
        } else {
            logger.error("No credentials provided, set to default, this can affect functionalities! ");
        }
    }

    private void createOutput(N5Writer n5, Metadata md) throws IOException {
        long[] dims = dimensionsAsLongArray(md.getBb());
        int[] blockSize = Arrays.stream(md.getBlocksize()).mapToInt(i -> (int) i).toArray();
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

}

