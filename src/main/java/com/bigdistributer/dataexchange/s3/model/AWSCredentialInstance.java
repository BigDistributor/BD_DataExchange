package com.bigdistributer.dataexchange.s3.model;

import com.amazonaws.auth.AWSCredentials;
import com.bigdistributer.dataexchange.s3.func.AWSCredentialsReader;
import com.bigdistributer.dataexchange.utils.DEFAULT;

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

//    private static void defaultInit() {
//        init(DEFAULT.AWS_CREDENTIALS_PATH);
//    }

    public static void main(String[] args) throws IllegalAccessException {
//        AWSCredentials credentials = AWSCredentialSingleton.get();
        AWSCredentials credentials = AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH).get();
        System.out.println("key: " + credentials.getAWSAccessKeyId());
    }
}
