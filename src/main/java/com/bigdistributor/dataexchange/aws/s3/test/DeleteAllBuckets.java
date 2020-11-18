package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.util.List;

public class DeleteAllBuckets {
    public static void main(String[] args) throws IllegalAccessException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);
        AmazonS3 s3 = S3BucketInstance.initS3(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

//        List Buckets
        List<Bucket> buckets = s3.listBuckets();

//        Delete all
        for (Bucket bucket : buckets) {
            System.out.println(bucket.toString());
            s3.deleteBucket(bucket.getName());
            System.out.println(bucket.getName() + " Deleted !");
        }
    }
}
