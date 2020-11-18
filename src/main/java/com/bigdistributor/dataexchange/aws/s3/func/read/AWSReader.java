package com.bigdistributor.dataexchange.aws.s3.func.read;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AWSReader {
    protected final S3BucketInstance bucketInstance;
    protected final String path;
    protected final String fileName;

    public AWSReader(S3BucketInstance bucketInstance, String path, String fileName) {
        this.bucketInstance = bucketInstance;
        this.path = path;
        this.fileName = fileName;
    }

    protected String get() throws IOException {
        S3Object object = bucketInstance.getS3().getObject(new GetObjectRequest(bucketInstance.getBucketName(), path + fileName));
        InputStream objectData = object.getObjectContent();
        String text;
        try (Reader reader = new InputStreamReader(objectData)) {
            text = CharStreams.toString(reader);
            System.out.println(text);
        }
        objectData.close();
        return text;
    }
}

