package com.bigdistributor.aws.spimloader;

import com.amazonaws.services.s3.AmazonS3;
import com.bigdistributor.aws.data.S3Utils;
import com.bigdistributor.core.spim.SpimDataLoader;
import com.bigdistributor.io.TempFolder;
import mpicbg.spim.data.SpimDataException;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import net.preibisch.mvrecon.fiji.spimdata.XmlIoSpimData2;

import java.io.File;

public class AWSSpimLoader implements SpimDataLoader {

    private final AmazonS3 s3;
    private final String uri;
    private File localFile;

    public AWSSpimLoader(AmazonS3 s3, String uri) {
        this.s3 = s3;
        this.uri = uri;
//        ImgLoaders.registerManually(XmlIoAWSSpimImageLoader.class);
    }

    public SpimData2 getSpimdata() {
        try {
            this.localFile = S3Utils.download(s3,TempFolder.get(),uri);

//            S3BucketInstance.get().downloadFrom(TempFolder.get(), path, new String[]{fileName, "interpolations"});
            return new XmlIoSpimData2("").load(localFile.getAbsolutePath());

        } catch (SpimDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}
