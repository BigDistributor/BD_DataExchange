package com.bigdistributor.dataexchange.aws.s3.test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.model.S3ClientInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;

import java.io.IOException;

public class ReadN5FromS3 {

    private final static String DATASET = "/volumes/raw";


    public static void main(String[] args) throws IllegalAccessException, IOException {
        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3ClientInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1);

        AmazonS3 s3 = S3ClientInstance.get();
        N5AmazonS3Reader reader = new N5AmazonS3Reader(s3, DEFAULT.id);

        RandomAccessibleInterval<FloatType> virtual = N5Utils.open(reader, DATASET);
//        ImageJFunctions.show(virtual, "From S3");
    }
}
