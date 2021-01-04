package com.bigdistributor.aws.spimloader;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.core.spim.SpimDataLoader;
import com.google.common.io.CharStreams;
import mpicbg.spim.data.SpimDataException;
import net.preibisch.mvrecon.fiji.spimdata.SpimData2;
import net.preibisch.mvrecon.fiji.spimdata.XmlIoSpimData2;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.StAXEventBuilder;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;

public class AWSSpimLoader implements SpimDataLoader {

    private static final String defaultName = "dataset.xml";

    private final S3BucketInstance bucketInstance;
    private final String path;
    private final String fileName;
    private Document doc;

    public AWSSpimLoader(S3BucketInstance bucketInstance, String path, String fileName) {
        this.bucketInstance = bucketInstance;
        this.path = path;
        this.fileName = fileName;
    }

    public AWSSpimLoader(S3BucketInstance s3, String path) {
        this(s3, path, defaultName);
    }

    private void read() throws IOException, JDOMException, XMLStreamException {
        if(doc!=null)
            return;
        S3Object object = bucketInstance.getS3().getObject(new GetObjectRequest(bucketInstance.getBucketName(), path + fileName));
        InputStream objectData = object.getObjectContent();
        String text = null;
        try (Reader reader = new InputStreamReader(objectData)) {
            text = CharStreams.toString(reader);
            System.out.println("XML load !");
            System.out.println(text);
        }
        objectData.close();
        doc =  parseXML(text);
    }

    private Document parseXML(String text) throws XMLStreamException, JDOMException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader reader = factory.createXMLEventReader(new StringReader(text));
        StAXEventBuilder builder = new StAXEventBuilder();
        return builder.build(reader);
    }

    public String getFile() {
        return fileName;
    }

    @Override
    public SpimData2 getSpimdata() {
        if (doc==null){
            try {
                read();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            return getSpimdata();
        }
        else{
            try {
                return new XmlIoSpimData2("").fromXml(doc.getRootElement(),new File(""));
            } catch (SpimDataException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
