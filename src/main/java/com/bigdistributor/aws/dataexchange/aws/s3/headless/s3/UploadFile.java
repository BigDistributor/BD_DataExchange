package com.bigdistributor.aws.dataexchange.aws.s3.headless.s3;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;

import java.io.File;

public class UploadFile {

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {

        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1,AWS_DEFAULT.bucket_name);

        File file = new File(AWS_DEFAULT.jar) ;

        S3BucketInstance.get().uploadFile(file);
    }
}
