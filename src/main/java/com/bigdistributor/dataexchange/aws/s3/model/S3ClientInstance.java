package com.bigdistributor.dataexchange.aws.s3.model;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.bigdistributor.dataexchange.job.model.JobID;

import java.util.List;

public class S3ClientInstance {
    static AmazonS3 instance;

    private S3ClientInstance(AmazonS3 s3client) {
        this.instance = s3client;
    }

    public synchronized static AmazonS3 get() throws IllegalAccessException {
        if (instance == null) {
            throw new IllegalAccessException("Init S3 client instance before request!");
        }
        return instance;
    }

    public static S3ClientInstance init(AWSCredentials credentials, Regions region) {

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        return new S3ClientInstance(s3client);
    }

    public static void showAll() throws IllegalAccessException {
        ListObjectsV2Result result = get().listObjectsV2(JobID.get());
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }
}
