package com.bigdistributor.dataexchange.aws.s3.func.auth;

import com.amazonaws.auth.AWSCredentials;
import com.bigdistributor.dataexchange.utils.DEFAULT;

public class AWSCredentialInstance {
    private static AWSCredentials instance;

    private AWSCredentialInstance(AWSCredentials credentials) {
        instance = credentials;
    }

    public static synchronized AWSCredentials get() throws IllegalAccessException {
        if (instance == null) {
            throw new IllegalAccessException("Init credential before!");
        }
        return instance;
    }

    public static synchronized AWSCredentialInstance init(String path) {
        AWSCredentialsReader reader = new AWSCredentialsReader(path);
        AWSCredentials credentials = reader.getCredentials();
        return new AWSCredentialInstance(credentials);
    }

    public static void main(String[] args) throws IllegalAccessException {
        AWSCredentials credentials = AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH).get();
        System.out.println("key: " + credentials.getAWSAccessKeyId());
    }
}
