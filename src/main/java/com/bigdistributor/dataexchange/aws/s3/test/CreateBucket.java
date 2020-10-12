package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Bucket;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.aws.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.util.List;

public class CreateBucket {
    public static void main(String[] args) throws IllegalAccessException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);
        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

//        Create Bucket
        S3ClientInstance.get().createBucket(JobID.get());

//        List Buckets
        List<Bucket> buckets = S3ClientInstance.get().listBuckets();

//        Delete all
        for(Bucket bucket : buckets) {
            System.out.println(bucket.toString());
        }


    }
}
