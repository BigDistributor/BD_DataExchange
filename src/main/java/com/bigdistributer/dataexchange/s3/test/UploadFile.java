package com.bigdistributer.dataexchange.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.bigdistributer.dataexchange.s3.model.AWSCredentialInstance;
import com.bigdistributer.dataexchange.s3.model.S3ClientInstance;
import com.bigdistributer.dataexchange.utils.DEFAULT;

import java.io.File;

public class UploadFile {
    private final static String TEST_FILE = "/Users/Marwan/Desktop/BigDistributer/test_files/file.tif";
    private final static String TEST_N5 = "/Users/Marwan/Desktop/BigDistributer/test_files/file.n5";

    static String bucket_name = "n5projecttest";

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
        S3ClientInstance.init(AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH).get(), Regions.EU_CENTRAL_1);
//        BucketManager manager = new BucketManager(S3ClientInstance.get(), bucket_name).create();
//        manager.upload(new File(TEST_FILE));
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(S3ClientInstance.get()).build();
        Upload upload = tm.upload(bucket_name,"123",new File(TEST_FILE));
        System.out.println("Started!");
//        upload.waitForCompletion();
        while (!upload.isDone()){
            System.out.print("\r t: "+ upload.getProgress().getBytesTransferred());
//            upload.wait(1000);
        }
        System.out.println("\nComplete");
    }
}
