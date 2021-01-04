package com.bigdistributor.aws.dataexchange.aws.s3.headless.s3;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.DEFAULT;

import java.io.File;

public class UploadFile {

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,DEFAULT.bucket_name);

        File file = new File(DEFAULT.jar) ;

        S3BucketInstance.get().uploadFile(file);
    }
}
