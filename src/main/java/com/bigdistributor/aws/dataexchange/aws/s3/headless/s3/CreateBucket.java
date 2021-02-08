package com.bigdistributor.aws.dataexchange.aws.s3.headless.s3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Bucket;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;

import java.util.List;

public class CreateBucket {
    private static final String bucket_name = "test_bucket_marwan";

    public static void main(String[] args) throws IllegalAccessException {

        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);
        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,bucket_name);

//        Create Bucket
        S3BucketInstance.get().createBucket();

//        List Buckets
        List<Bucket> buckets = S3BucketInstance.get().getS3().listBuckets();

//        Delete all
        for(Bucket bucket : buckets) {
            System.out.println(bucket.toString());
        }


    }
}
