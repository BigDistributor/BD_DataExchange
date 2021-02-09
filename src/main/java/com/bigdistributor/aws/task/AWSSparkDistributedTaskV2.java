package com.bigdistributor.aws.task;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.read.AWSReader;
import com.bigdistributor.aws.spimloader.AWSSpimLoader;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.core.blockmanagement.blockinfo.BasicBlockInfo;
import com.bigdistributor.core.spim.SpimDataLoader;
import com.bigdistributor.core.task.BlockTask;
import com.bigdistributor.core.task.JobID;
import com.bigdistributor.core.task.items.Metadata;
import com.bigdistributor.core.task.items.SerializableParams;
import com.bigdistributor.io.mvrecon.SpimHelpers;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.janelia.saalfeldlab.n5.DataType;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.concurrent.Callable;

// T: DataType exmpl FloatType
// K: Task Param
public class AWSSparkDistributedTaskV2<T extends NativeType<T>, K extends SerializableParams> implements Callable<Void> {
    @Option(names = {"-o", "--output"}, required = true, description = "The path of the output Data")
    String output;

    @Option(names = {"-i", "--input"}, required = true, description = "The path of the input Data inside bucket")
    String input;

    @Option(names = {"-b", "--bucket"}, required = true, description = "The name of bucket")
    String bucketName;

    @Option(names = {"-c", "--cred"}, required = false, description = "The path of credentials")
    String awsCredentialPath;

    @Option(names = {"-id", "--jobid"}, required = true, description = "The jod Id")
    String jobId;

    @Option(names = {"-m", "--metadata"}, required = true, description = "The path of the MetaData file")
    String metadataPath;

    @Option(names = {"-p", "--param"}, required = false, description = "The path of the params file")
    String paramPath;

    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private BlockTask mainTask;

    private Metadata md;
    private static String dataset = "/volumes/raw";

    public AWSSparkDistributedTaskV2(BlockTask task) {
        this.mainTask = task;
    }

    @Override
    public Void call() throws Exception {
        JobID.set(jobId);
        if(awsCredentialPath!= null)
            AWSCredentialInstance.init(awsCredentialPath);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, bucketName);

        md = Metadata.fromJsonString(new AWSReader(S3BucketInstance.get(),"",metadataPath).get());
        logger.info("Got metadata !");
        if (md == null) {
            logger.error("Error metadata file !");
            return null;
        }

        logger.info(JobID.get() + " started!");
        SerializableParams<K> params = null;
        if (paramPath!=null)
        try{
            params = new SerializableParams<K>().fromJsonString(new AWSReader(S3BucketInstance.get(),"",paramPath).get());
        }catch (Exception e){
            logger.error("Invalid params: "+e.toString());
        }

        SpimDataLoader loader = new AWSSpimLoader(S3BucketInstance.get(), "", input);
        SpimData2 spimdata = loader.getSpimdata();
        logger.info("Got spimdata");

        SparkConf sparkConf = new SparkConf().setAppName(JobID.get()).set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        N5Writer n5 = new N5AmazonS3Writer(S3BucketInstance.get().getS3(), bucketName, output);

        createOutput(n5, md);
        final JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        SerializableParams<K> finalParams = params;
        sparkContext.parallelize(md.getBlocksInfo(), md.getBlocksInfo().size()).foreach((VoidFunction<BasicBlockInfo>) binfo -> {
            int blockID = binfo.getBlockId();
            logger.blockStarted(blockID, " start processing..");
            SpimHelpers.getBb(binfo);
            RandomAccessibleInterval<T> result = mainTask.blockTask(spimdata, finalParams, SpimHelpers.getBb(binfo));
            logger.blockLog(blockID, " Got processed image");
            N5Utils.saveBlock(result, n5, dataset, binfo.getGridOffset());
            logger.blockDone(blockID, " Task done.");
        });
        return null;
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

