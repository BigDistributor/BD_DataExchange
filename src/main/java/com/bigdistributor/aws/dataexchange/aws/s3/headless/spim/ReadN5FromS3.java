package com.bigdistributor.aws.dataexchange.aws.s3.headless.spim;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;

import java.io.IOException;


public class ReadN5FromS3 {

    private final static String DATASET = "/volumes/raw";
    private final static String bucket_name = "mzouink-test";



    public static void main(String[] args) throws IllegalAccessException, IOException {
//        N5FSReader

        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, bucket_name);

        ListObjectsV2Result x = S3BucketInstance.get().getS3().listObjectsV2(bucket_name);

        N5AmazonS3Reader reader = new N5AmazonS3Reader(S3BucketInstance.get().getS3(), S3BucketInstance.get().getBucketName(),"output.n5/");

        RandomAccessibleInterval<FloatType> virtual = N5Utils.open(reader, DATASET);
//        ImageJFunctions.show(virtual);
//        BdvFunctions.show(virtual);

//        ImageJFunctions.show(virtual, "From S3");
    }
}
