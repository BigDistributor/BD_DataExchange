package com.bigdistributor.aws.data;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.Serializable;

public class CredentialSupplier implements Serializable {
    private final String credPublicKey;
    private final String credPrivateKey;

    public CredentialSupplier(String credPublicKey, String credPrivateKey) {
        this.credPublicKey = credPublicKey;
        this.credPrivateKey = credPrivateKey;
    }

    public AWSCredentials getCredentials() {
        AWSCredentials credentials = new BasicAWSCredentials(
                credPublicKey, credPrivateKey
        );
        return credentials;
    }

    public AmazonS3 getS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();

    }
}
