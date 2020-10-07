package com.bigdistributor.dataexchange.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.bigdistributor.dataexchange.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.s3.model.JobID;
import com.bigdistributor.dataexchange.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.io.File;

public class UploadFile {
    private final static String TEST_FILE = "/Users/Marwan/Desktop/BigDistributor/test_files/file.tif";
    private final static String TEST_N5 = "/Users/Marwan/Desktop/BigDistributor/test_files/file.n5";


    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

        S3ClientInstance.get().createBucket(JobID.get());

//        BucketManager manager = new BucketManager(S3ClientInstance.get(), bucket_name).create();
//        manager.upload(new File(TEST_FILE));
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(S3ClientInstance.get()).build();
        File file = new File(TEST_FILE) ;

        Upload upload = tm.upload(JobID.get(),file.getName(),file);

        System.out.println("Started!");
//        upload.waitForCompletion();
//        while (!upload.isDone()){
//            System.out.print("\r progress : "+ upload.getProgress().getBytesTransferred());
////            upload.wait(1000);
//        }
        System.out.println("\nComplete");
    }
}
