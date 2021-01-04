package com.bigdistributor.aws.dataexchange.aws.s3.func.read;

import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.DEFAULT;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.StAXEventBuilder;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringReader;


public class AWSXMLReader extends AWSReader {

    private static final String defaultName = "dataset.xml";

    public AWSXMLReader(S3BucketInstance bucketInstance, String path, String fileName) {
        super(bucketInstance, path, fileName);

    }

    public AWSXMLReader(S3BucketInstance s3, String path) {
        this(s3, path, defaultName);
    }


    public Document read() throws IOException, JDOMException, XMLStreamException {
        return parseXML(super.get());
    }

    private Document parseXML(String text) throws XMLStreamException, JDOMException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader reader = factory.createXMLEventReader(new StringReader(text));
        StAXEventBuilder builder = new StAXEventBuilder();
        return builder.build(reader);
    }

    public static void main(String[] args) throws IllegalAccessException, IOException, JDOMException, XMLStreamException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, DEFAULT.bucket_name);

        Document xml = new AWSXMLReader(S3BucketInstance.get(), "big/").read();

        System.out.println(xml);
    }
}
