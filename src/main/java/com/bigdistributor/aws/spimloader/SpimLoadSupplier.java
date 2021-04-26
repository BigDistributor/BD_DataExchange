package com.bigdistributor.aws.spimloader;

import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.aws.data.CredentialSupplier;

import java.io.Serializable;

public class SpimLoadSupplier implements Serializable {
    private final String input;
    private final CredentialSupplier credSupplier;


    public SpimLoadSupplier(CredentialSupplier credSupplier, String input) {
        this.credSupplier  = credSupplier;
        this.input = input;
    }

    public AWSSpimLoader getLoader() {
        return AWSSpimLoader.init(getS3(), input);
    }

    public AmazonS3 getS3() {
        return credSupplier.getS3();
    }
}
