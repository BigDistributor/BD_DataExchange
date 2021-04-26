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
    private final String region;

    public CredentialSupplier(String credPublicKey, String credPrivateKey){
        this(credPublicKey,credPrivateKey,"eu-central-1");
    }


    public static CredentialSupplier get(){
        return CredentialConfig.get();
    }

    public CredentialSupplier(String credPublicKey, String credPrivateKey, String region) {
        this.credPublicKey = credPublicKey;
        this.credPrivateKey = credPrivateKey;
        this.region = region;
        CredentialConfig.set(this);
    }

    public AWSCredentials getCredentials() {
        AWSCredentials credentials = new BasicAWSCredentials(
                credPublicKey, credPrivateKey
        );
        return credentials;
    }

    public AmazonS3 getS3() {
        Regions s3Region = Regions.fromName(region);
        System.out.println("Region: "+s3Region.getName());
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion(s3Region)
                .build();

    }

    @Override
    public String toString() {
        return "CredentialSupplier{" +
                "credPublicKey='" + credPublicKey + '\'' +
                ", credPrivateKey='" + credPrivateKey + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
