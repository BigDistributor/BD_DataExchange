package com.bigdistributer.dataexchange.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Bucket;
import com.bigdistributer.dataexchange.s3.model.AWSCredentialInstance;
import com.bigdistributer.dataexchange.s3.model.S3ClientInstance;
import com.bigdistributer.dataexchange.utils.DEFAULT;

import java.util.List;

public class DeleteAllBuckets {
    public static void main(String[] args) throws IllegalAccessException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);
        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

//        List Buckets
        List<Bucket> buckets = S3ClientInstance.get().listBuckets();

//        Delete all
        for (Bucket bucket : buckets) {
            System.out.println(bucket.toString());
            S3ClientInstance.get().deleteBucket(bucket.getName());
            System.out.println(bucket.getName() + " Deleted !");
        }
    }
}
