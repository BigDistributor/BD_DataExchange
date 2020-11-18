package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.io.File;

public class UploadFile {

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,DEFAULT.id);

        File file = new File(DEFAULT.jar) ;

        S3BucketInstance.get().uploadFile(file);
    }
}
