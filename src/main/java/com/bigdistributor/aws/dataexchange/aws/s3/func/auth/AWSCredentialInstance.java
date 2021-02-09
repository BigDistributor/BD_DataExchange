package com.bigdistributor.aws.dataexchange.aws.s3.func.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;
import com.bigdistributor.biglogger.adapters.Log;

import java.lang.invoke.MethodHandles;

public class AWSCredentialInstance {
    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static AWSCredentials instance;

    private AWSCredentialInstance(AWSCredentials credentials) {
        instance = credentials;
    }

    public static synchronized AWSCredentials get() throws IllegalAccessException {
        if (instance == null) {
            init("");
//            throw new IllegalAccessException("Init credential before!");
        }
        return instance;
    }

    public static synchronized AWSCredentialInstance init(String path) {
        AWSCredentials credentials;
        try{
        AWSCredentialsReader reader = new AWSCredentialsReader(path);
        credentials = reader.getCredentials();}
        catch (Exception e){
            logger.warning("No credentials evaluable, set default");
            credentials = new ProfileCredentialsProvider().getCredentials();
        }
        return new AWSCredentialInstance(credentials);
    }

    public static void main(String[] args) throws IllegalAccessException {
        AWSCredentials credentials = AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH).get();
        System.out.println("key: " + credentials.getAWSAccessKeyId());
    }
}
