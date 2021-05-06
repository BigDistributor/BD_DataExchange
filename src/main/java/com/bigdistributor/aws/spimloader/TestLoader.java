package com.bigdistributor.aws.spimloader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.utils.AWS_DEFAULT;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import org.jdom2.JDOMException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class TestLoader {
    public static void main(String[] args) throws IllegalAccessException, IOException, JDOMException, XMLStreamException {

        AWSCredentials credentials = AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH).get();
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();

        AWSSpimLoader loader = AWSSpimLoader.init(s3client, "s3://mzouink-test/dataset-n5.xml");
        SpimData2 data = loader.getSpimdata();
        System.out.println(data.toString());

        System.out.println(loader.getN5uri());


    }
}
