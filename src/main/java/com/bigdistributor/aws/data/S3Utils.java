package com.bigdistributor.aws.data;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.google.common.io.CharStreams;

import java.io.*;

public class S3Utils {

    public static File download(AmazonS3 s3, File localFolder, String uri) {
        try {
            TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
            AmazonS3URI amazonS3URI = new AmazonS3URI(uri);
            System.out.println("File path: " + amazonS3URI.getKey());
            File localFile = new File(localFolder, amazonS3URI.getKey());
            System.out.println("Local file: " + localFile);
            Download upload = tm.download(amazonS3URI.getBucket(), amazonS3URI.getKey(), localFile);
            upload.waitForCompletion();
            return localFile;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File: " + uri + " not found!");
        }
        return null;
    }

    public static String get(AmazonS3 s3,  String uri) throws IOException {
        AmazonS3URI amazonS3URI = new AmazonS3URI(uri);
        GetObjectRequest request = new GetObjectRequest(amazonS3URI.getBucket(),amazonS3URI.getKey());
        System.out.println("Getting file: "+request.getKey() + " from bucket "+ request.getBucketName());
        S3Object object = s3.getObject(request);
        InputStream objectData = object.getObjectContent();
        String text;
        try (Reader reader = new InputStreamReader(objectData)) {
            text = CharStreams.toString(reader);
//            System.out.println(text);
        }
        objectData.close();
//        System.out.println("Text got : " +text);
        return text;
    }
}
