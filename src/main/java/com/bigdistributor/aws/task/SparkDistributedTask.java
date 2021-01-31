package com.bigdistributor.aws.task;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.spimloader.AWSSpimLoader;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.core.blockmanagement.blockinfo.BasicBlockInfo;
import com.bigdistributor.core.spim.SpimDataLoader;
import com.bigdistributor.core.task.BlockTask;
import com.bigdistributor.core.task.JobID;
import com.bigdistributor.core.task.items.Metadata;
import com.bigdistributor.core.task.items.SerializableParams;
import com.bigdistributor.io.mvrecon.SpimHelpers;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;
import picocli.CommandLine.Option;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

// T: DataType exmpl FloatType
// K: Task Param
public class SparkDistributedTask<T extends NativeType<T>, K extends SerializableParams> implements Callable<Void> {
    @Option(names = {"-o", "--output"}, required = true, description = "The path of the output Data")
    String output;

    @Option(names = {"-i", "--input"}, required = true, description = "The path of the input Data inside bucket")
    String input;

    @Option(names = {"-b", "--bucket"}, required = true, description = "The name of bucket")
    String bucketName;

    @Option(names = {"-c", "--cred"}, required = true, description = "The path of credentials")
    String awsCredentialPath;

    @Option(names = {"-id", "--jobid"}, required = true, description = "The jod Id")
    String jobId;

    @Option(names = {"-m", "--meta"}, required = true, description = "The path of the MetaData file")
    String metadataPath;

    @Option(names = {"-p", "--param"}, required = false, description = "The path of the params file")
    String paramPath;

    @Option(names = {"-d", "--dataset"}, required = false, description = "name of xml file")
    String xmlFile= "dataset.xml";

    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private BlockTask mainTask;

    private Metadata md;
    private static String dataset = "/volumes/raw";

    public SparkDistributedTask(BlockTask task) {
        this.mainTask = task;
    }

    @Override
    public Void call() throws Exception {

        md = Metadata.fromJson(metadataPath);
        if (md == null) {
            logger.error("Error metadata file !");
            return null;
        }
        JobID.set(jobId);
        logger.info(jobId + " started!");
        SerializableParams<K> params = null;
        if (paramPath != null) {
            File paramFile = new File(paramPath);
            params = new SerializableParams<K>().fromJson(paramFile);
        }

        AWSCredentialInstance.init(awsCredentialPath);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, bucketName);

        SpimDataLoader loader = new AWSSpimLoader(S3BucketInstance.get(), input, xmlFile);
        SpimData2 spimdata = loader.getSpimdata();
        logger.info("Got spimdata");

        SparkConf sparkConf = new SparkConf().setAppName(jobId).set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );;
        N5Writer n5 = new N5AmazonS3Writer(S3BucketInstance.get().getS3(),output);
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

}

