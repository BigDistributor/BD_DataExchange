package com.bigdistributor.dataexchange.aws.s3.func;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.model.S3BucketInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class XMLReader {

    private static final String defaultName = "dataset.xml";

    private final S3BucketInstance bucketInstance;
    private final String path;
    private final String fileName;

    public XMLReader(S3BucketInstance bucketInstance, String path, String fileName) {
        this.bucketInstance = bucketInstance;
        this.path = path;
        this.fileName = fileName;
    }

    public XMLReader(S3BucketInstance s3, String path) {
        this(s3, path, defaultName);
    }

    public String read() throws IllegalAccessException, IOException {
        S3Object object = bucketInstance.getS3().getObject(new GetObjectRequest(bucketInstance.getBucketName(), path + fileName));
        InputStream objectData = object.getObjectContent();
        // Process the objectData stream.
        String text = null;
        try (Reader reader = new InputStreamReader(objectData)) {
            text = CharStreams.toString(reader);
            System.out.println(text);
        }
        objectData.close();
        return text;

    }


    public static void main(String[] args) throws IllegalAccessException, IOException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, DEFAULT.bucket_name);

        String xml = new XMLReader(S3BucketInstance.get(), "big/").read();

        System.out.println(xml);
    }
}
