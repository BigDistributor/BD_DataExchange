package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.model.S3BucketInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.io.File;
import java.io.IOException;

public class UploadFolder {
    private final static String TEST_N5 = "/Users/Marwan/Desktop/BigDistributer/test_files/dataset.n5";

    public static void main(String[] args) throws IllegalAccessException, InterruptedException, IOException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,DEFAULT.id);

        File file = new File(TEST_N5);
        S3BucketInstance.get().upload(file);

    }
}
