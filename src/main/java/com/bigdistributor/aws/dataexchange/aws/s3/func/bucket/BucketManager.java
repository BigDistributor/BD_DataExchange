package com.bigdistributor.aws.dataexchange.aws.s3.func.bucket;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bigdistributor.biglogger.adapters.Log;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class BucketManager {
    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    AmazonS3 s3client;
    String name;

    public BucketManager(AmazonS3 s3client, String name) {
        this.s3client = s3client;
        this.name = name;
    }

    public BucketManager create() {
        if (!bucketExist(name, s3client.listBuckets())) {
            s3client.createBucket(name);
            logger.info("Bucket created!");
        } else {
            logger.warning("Bucket exists!");
        }
        return this;
    }

    private boolean bucketExist(String name, List<Bucket> listBuckets) {
        for (Bucket b : listBuckets)
            if (name.equals(b.getName()))
                return true;
        return false;
    }

    public void upload(File file) throws IOException {
        if (!file.exists())
            throw new IOException("File not exist !");

        if (file.isDirectory())
            throw new RuntimeException("Not implimented yet !");

        if (file.isFile())
            s3client.putObject(name, file.getName(), file);
        logger.info("File " + file.getName() + " sent!");
    }

    public void upload(List<File> files) {
        List<PutObjectRequest> requestList = new ArrayList<>();
        for (File f : files)
            requestList.add(new PutObjectRequest(name, "f/" + f.getName(), f));
        for (PutObjectRequest request : requestList) {
            s3client.putObject(request);
            logger.info("File " + request.getFile().getName() + " sent!");
        }
    }
}

