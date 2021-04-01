package com.bigdistributor.aws.data;

import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.core.data.OutputData;
import org.janelia.saalfeldlab.n5.N5Reader;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;

import java.io.IOException;

public class AWSOutputN5Data implements OutputData {

    private final String bucketName;
    private final String path;
    private final String dataset;
    private final AmazonS3 s3;

    public AWSOutputN5Data(AmazonS3 s3, String bucketName, String path, String fileName, String dataset) {
        this.s3 = s3;
        this.bucketName = bucketName;
        this.path = path;
        this.dataset = dataset;
    }


    @Override
    public N5Reader getReader() throws IOException {
        return new N5AmazonS3Reader(s3, bucketName, path);

    }

    @Override
    public N5Writer getWriter() throws IOException {
        return new N5AmazonS3Writer(s3, bucketName, path);
    }
}
