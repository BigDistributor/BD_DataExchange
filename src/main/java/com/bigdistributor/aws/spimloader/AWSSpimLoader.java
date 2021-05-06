package com.bigdistributor.aws.spimloader;

import bdv.img.aws.XmlIoAWSSpimImageLoader;
import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.aws.dataexchange.aws.s3.S3Utils;
import com.bigdistributor.core.spim.SpimDataLoader;
import com.bigdistributor.io.TempFolder;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.generic.sequence.ImgLoaders;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import net.preibisch.mvrecon.fiji.spimdata.XmlIoSpimData2;

import java.io.File;

public class AWSSpimLoader implements SpimDataLoader {

    private static AWSSpimLoader instance;

    private final AmazonS3 s3;
    private final String uri;
    private File localFile;
    private String n5uri;

    private AWSSpimLoader(AmazonS3 s3, String uri) {
        this.s3 = s3;
        this.uri = uri;
        ImgLoaders.registerManually(XmlIoAWSSpimImageLoader.class);
    }

    public static AWSSpimLoader get() {
        return instance;
    }

    public AmazonS3 getS3() {
        return s3;
    }

    public String getUri() {
        return uri;
    }

    public String getFileUri(String file) {
        String[] elms = uri.split("/");
        elms[elms.length - 1] = file;
        String updatedUri = String.join("/", elms);
        return updatedUri;
    }

    public static AWSSpimLoader init(AmazonS3 s3, String uri) {
        instance = new AWSSpimLoader(s3, uri);
        return instance;
    }

    public SpimData2 getSpimdata() {
        try {
            this.localFile = S3Utils.download(s3, TempFolder.get(), uri);

//            S3BucketInstance.get().downloadFrom(TempFolder.get(), path, new String[]{fileName, "interpolations"});
            return new XmlIoSpimData2("").load(localFile.getAbsolutePath());

        } catch (SpimDataException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setN5Uri(String uri) {
        System.out.println("N5 uri: "+uri);
        this.n5uri = uri;
    }

    public String getN5uri() {
        return n5uri;
    }
}
