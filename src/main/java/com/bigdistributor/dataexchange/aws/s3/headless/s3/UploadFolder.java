package com.bigdistributor.dataexchange.aws.s3.headless.s3;

import com.amazonaws.regions.Regions;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.io.File;
import java.io.IOException;

public class UploadFolder {
    private final static String folder = "/Users/Marwan/Desktop/Task/data/n5aws/";

    public static void main(String[] args) throws IllegalAccessException, InterruptedException, IOException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, DEFAULT.id);


        File file = new File(folder);
        for (File f : file.listFiles())
            S3BucketInstance.get().upload(f);

    }
}
