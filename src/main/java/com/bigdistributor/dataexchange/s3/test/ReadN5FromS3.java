package com.bigdistributor.dataexchange.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.dataexchange.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.s3.model.JobID;
import com.bigdistributor.dataexchange.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.utils.DEFAULT;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;

import net.imglib2.img.display.imagej.ImageJFunctions;
import java.io.IOException;

public class ReadN5FromS3 {

    private final static String DATASET = "/volumes/raw";

    private final static String id = "2020-10-05-2eef10e2307f412cb0d0a522717ba7ec";

    public static void main(String[] args) throws IllegalAccessException, IOException {
        JobID.set(id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

        AmazonS3 s3 = S3ClientInstance.get();
        N5AmazonS3Reader reader = new N5AmazonS3Reader(s3,id);

        RandomAccessibleInterval<FloatType> virtual = N5Utils.open(reader, DATASET);
        ImageJFunctions.show(virtual,"From S3");

    }
}
