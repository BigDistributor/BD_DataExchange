package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Bucket;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.util.List;

public class CreateBucket {
    public static void main(String[] args) throws IllegalAccessException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);
        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,JobID.get());

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
