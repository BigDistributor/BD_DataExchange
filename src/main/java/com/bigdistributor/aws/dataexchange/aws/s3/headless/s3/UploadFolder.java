package com.bigdistributor.aws.dataexchange.aws.s3.headless.s3;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.DEFAULT;

import java.io.File;
import java.io.IOException;

public class UploadFolder {
    private final static String folder = "/Users/Marwan/Desktop/Task/data/big/n5/";

    public static void main(String[] args) throws IllegalAccessException, InterruptedException, IOException {
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, "bigstitcher");

        File file = new File(folder);
//        for (File f : file.listFiles())
        S3BucketInstance.get().upload(file);
    }


}
