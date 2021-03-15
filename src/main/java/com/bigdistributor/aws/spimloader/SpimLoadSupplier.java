package com.bigdistributor.aws.spimloader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.bigdistributor.aws.spimloader.AWSSpimLoader;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;

import java.io.IOException;
import java.io.Serializable;

public class SpimLoadSupplier implements Serializable {

    private final String credPublicKey;
    private final String bucketName;
    private final String credPrivateKey;
    private final String input;
    private final String output;

    public SpimLoadSupplier(String credPublicKey, String credPrivateKey, String bucketName, String input,String output) {
        this.credPublicKey = credPublicKey;
        this.credPrivateKey = credPrivateKey;
        this.bucketName = bucketName;
        this.input = input;
        this.output = output;
    }

    public AWSSpimLoader getLoader() {
        return new AWSSpimLoader(getS3(), bucketName, "", input);
    }

    public AmazonS3 getS3() {
        AWSCredentials credentials = new BasicAWSCredentials(
                credPublicKey, credPrivateKey
        );
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    public N5Writer getN5Output() throws IOException {
        return new N5AmazonS3Writer(getS3(), bucketName, output);
    }
}
