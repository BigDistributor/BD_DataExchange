package com.bigdistributor.aws.data;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import org.janelia.saalfeldlab.n5.N5Reader;
import org.janelia.saalfeldlab.n5.N5Writer;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Reader;
import org.janelia.saalfeldlab.n5.s3.N5AmazonS3Writer;

import java.io.IOException;
import java.io.Serializable;

public class AWSN5Supplier implements Serializable {

    private final String s3uri;
    private final CredentialSupplier credentialSupplier;

    public AWSN5Supplier(String s3uri, CredentialSupplier credentialSupplier) {
        this.credentialSupplier = credentialSupplier;
        this.s3uri = s3uri;

        AmazonS3URI uri = new AmazonS3URI(s3uri);
        System.out.println("Supplier init " + s3uri + " bucket: " + uri.getBucket() + " file: " + uri.getKey());
    }

    public AmazonS3 getS3() {
        AmazonS3 s3 = credentialSupplier.getS3();

        System.out.println("Got S3: " + s3.getRegionName());
        return s3;
    }

    public N5Reader getReader() throws IOException {
        AmazonS3 s3 = getS3();
        return new N5AmazonS3Reader(s3, new AmazonS3URI(s3uri));
    }

    public N5Writer getWriter() throws IOException {
        AmazonS3 s3 = getS3();
        return new N5AmazonS3Writer(s3, new AmazonS3URI(s3uri));
    }

    public boolean exists() {
        AmazonS3URI uri = new AmazonS3URI(s3uri);
        if (getS3().listObjectsV2(uri.getBucket(), uri.getKey()).getKeyCount() > 0)
            return true;
        return false;
    }
}
