package com.bigdistributor.dataexchange.aws.s3.func.bucket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.bigdistributor.dataexchange.job.model.JobID;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class S3BucketInstance {

    private static S3BucketInstance instance;
    private AmazonS3 s3;
    private String bucketName;

    private S3BucketInstance(AmazonS3 s3client, String bucket) {
        this.s3 = s3client;
        this.bucketName = bucket;
    }

    public synchronized static S3BucketInstance get() throws IllegalAccessException {
        if (instance == null) {
            throw new IllegalAccessException("Init S3 client instance before request!");
        }
        return instance;
    }

    public static S3BucketInstance init(AWSCredentials credentials, Regions region, String bucket) {
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        instance = new S3BucketInstance(s3client, bucket);
        return instance;
    }

    public static AmazonS3 initS3(AWSCredentials credentials, Regions region) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    public void upload(File file) throws IOException, InterruptedException {

        if (!file.exists())
            throw new IOException(file.getAbsolutePath() + " not exist ! ");
        if (file.isDirectory())
            uploadFolder(file);
        else if (file.isFile())
            uploadFile(file);


    }

    public void uploadFolder(File file) throws InterruptedException {
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
        MultipleFileUpload upload = tm.uploadDirectory(JobID.get(), file.getName(), file, true);
        System.out.println("uploading "+file.getAbsolutePath());
        upload.waitForCompletion();

        System.out.println("Complete");
    }

    public void createBucket() {
        s3.createBucket(bucketName);
    }

    public void showAll() throws IllegalAccessException {
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }

    public AmazonS3 getS3() {
        return s3;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void uploadFile(File file) throws InterruptedException {
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
        Upload upload = tm.upload(bucketName, file.getName(), file);
        System.out.println("uploading "+file.getAbsolutePath());
        upload.waitForCompletion();
        System.out.println("Complete");
    }
}
