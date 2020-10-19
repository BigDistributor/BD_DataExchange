package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.aws.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;

import java.io.File;
import java.io.IOException;

public class UploadFolder {
    private final static String TEST_N5 = "/Users/Marwan/Desktop/BigDistributer/test_files/dataset.n5";



    public static void main(String[] args) throws IllegalAccessException, InterruptedException, IOException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

//        S3ClientInstance.get().createBucket(JobID.get());

        TransferManager tm = TransferManagerBuilder.standard().withS3Client(S3ClientInstance.get()).build();
        File file = new File(TEST_N5);

        if (!file.exists())
            throw new IOException(file.getAbsolutePath()+" not exist ! ");
        if (!file.isDirectory())
            throw new IOException(file.getAbsolutePath()+" Not folder ! ");

        MultipleFileUpload upload = tm.uploadDirectory(JobID.get(), "source.n5", file, true);

        System.out.println("Started!");
        upload.waitForCompletion();
//        while (!upload.isDone()){
//            System.out.print("\r progress : "+ upload.getProgress().getBytesTransferred());
////            upload.wait(1000);
//        }
        System.out.println("\nComplete");
    }
}
