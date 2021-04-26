package com.bigdistributor.aws.spimloader;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.utils.AWS_DEFAULT;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import org.jdom2.JDOMException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class TestLoader {
    public static void main(String[] args) throws IllegalAccessException, IOException, JDOMException, XMLStreamException {

        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, AWS_DEFAULT.bucket_name,"big/");

        AWSSpimLoader loader = AWSSpimLoader.init(S3BucketInstance.get().getS3(), "s3://mzouink-test/big/dataset.xml");
        SpimData2 data = loader.getSpimdata();
        System.out.println(data.toString());
    }
}
