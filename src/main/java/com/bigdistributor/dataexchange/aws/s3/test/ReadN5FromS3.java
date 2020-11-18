package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;

import java.io.IOException;

public class ReadN5FromS3 {

    private final static String DATASET = "/volumes/raw";


    public static void main(String[] args) throws IllegalAccessException, IOException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, DEFAULT.id);

        AmazonS3 s3 = S3BucketInstance.get().getS3();
        N5AmazonS3Reader reader = new N5AmazonS3Reader(S3BucketInstance.get().getS3(), S3BucketInstance.get().getBucketName());

//        RandomAccessibleInterval<FloatType> virtual = N5Utils.open(reader, DATASET);
//        ImageJFunctions.show(virtual, "From S3");
    }
}
