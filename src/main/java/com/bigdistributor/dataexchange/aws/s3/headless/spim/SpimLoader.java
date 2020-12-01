package com.bigdistributor.dataexchange.aws.s3.headless.spim;

import bdv.spimdata.SequenceDescriptionMinimal;
import com.amazonaws.regions.Regions;
import com.bigdistributor.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.dataexchange.aws.s3.func.read.AWSXMLReader;
import com.bigdistributor.dataexchange.utils.DEFAULT;
import mpicbg.spim.data.XmlHelpers;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.registration.ViewRegistration;
import mpicbg.spim.data.registration.ViewRegistrations;
import mpicbg.spim.data.sequence.*;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.realtransform.AffineTransform3D;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//package bdv.spimdata.legacy.XmlIoSpimDataMinimalLegacy.java
public class SpimLoader {
    public static void main(String[] args) throws IllegalAccessException, JDOMException, XMLStreamException, IOException {

        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, DEFAULT.bucket_name);


        Document doc = new AWSXMLReader(S3BucketInstance.get(), "big/").read();

        System.out.println("Got file  !");
        System.out.println("Value: " +doc.getRootElement().getChild("SequenceDescription").getChild("Timepoints").getAttributeValue("type"));
        for (Element a : doc.getRootElement().getChild("SequenceDescription").getChild("Timepoints").getChildren()) {
            System.out.println(a.getName());
        }
        Element root = doc.getRootElement().getChild("SequenceDescription");


        final TimePoints timepoints = createTimepointsFromXml(root);
        final Map<Integer, ? extends BasicViewSetup> setups = createViewSetupsFromXml(root);
        final MissingViews missingViews = null;
        final SequenceDescriptionMinimal sequenceDescription = new SequenceDescriptionMinimal(timepoints, setups, null, missingViews);
        final ViewRegistrations viewRegistrations = createRegistrationsFromXml(root);

        System.out.println("Timepoint");
        System.out.println("Size: " + timepoints.size());
        for (Map.Entry<Integer, TimePoint> entry : timepoints.getTimePoints().entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue().getId() + "-" + entry.getValue().getName());
        }

        System.out.println();

        System.out.println("setups");
        System.out.println("Size: " + setups.size());
        for (Map.Entry<Integer, ? extends BasicViewSetup> entry : setups.entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue().getId() + "-" + entry.getValue().getName());
        }
    }

    protected static ViewRegistrations createRegistrationsFromXml(final Element sequenceDescriptionElem) {
        final Element elem = sequenceDescriptionElem.getChild("ViewRegistrations");
        final ArrayList<ViewRegistration> regs = new ArrayList<>();
        for (final Element vr : elem.getChildren("ViewRegistration")) {
            final int timepointId = XmlHelpers.getInt(vr, "timepoint");
            final int setupId = XmlHelpers.getInt(vr, "setup");
            final AffineTransform3D transform = XmlHelpers.getAffineTransform3D(vr, "affine");
            regs.add(new ViewRegistration(timepointId, setupId, transform));
        }
        return new ViewRegistrations(regs);
    }

    private static TimePoints createTimepointsFromXml(final Element sequenceDescription) {
        final Element timepoints = sequenceDescription.getChild("Timepoints");
        final String type = timepoints.getAttributeValue("type");
        if (type.equals("range")) {
            final int first = Integer.parseInt(timepoints.getChildText("first"));
            final int last = Integer.parseInt(timepoints.getChildText("last"));
            final ArrayList<TimePoint> tps = new ArrayList<>();
            for (int i = first, t = 0; i <= last; ++i, ++t)
                tps.add(new TimePoint(t));
            return new TimePoints(tps);
        } else {
            throw new RuntimeException("unknown <Timepoints> type: " + type);
        }
    }

    private static Map<Integer, ? extends BasicViewSetup> createViewSetupsFromXml(final Element sequenceDescription) {
        final HashMap<Integer, BasicViewSetup> setups = new HashMap<>();
        final HashMap<Integer, Angle> angles = new HashMap<>();
        final HashMap<Integer, Channel> channels = new HashMap<>();
        final HashMap<Integer, Illumination> illuminations = new HashMap<>();

        for (final Element elem : sequenceDescription.getChildren("ViewSetup")) {
            final int id = XmlHelpers.getInt(elem, "id");

            final int angleId = XmlHelpers.getInt(elem, "angle");
            Angle angle = angles.get(angleId);
            if (angle == null) {
                angle = new Angle(angleId);
                angles.put(angleId, angle);
            }

            final int illuminationId = XmlHelpers.getInt(elem, "illumination");
            Illumination illumination = illuminations.get(illuminationId);
            if (illumination == null) {
                illumination = new Illumination(illuminationId);
                illuminations.put(illuminationId, illumination);
            }

            final int channelId = XmlHelpers.getInt(elem, "channel");
            Channel channel = channels.get(channelId);
            if (channel == null) {
                channel = new Channel(channelId);
                channels.put(channelId, channel);
            }

            final long w = XmlHelpers.getInt(elem, "width");
            final long h = XmlHelpers.getInt(elem, "height");
            final long d = XmlHelpers.getInt(elem, "depth");
            final Dimensions size = new FinalDimensions(w, h, d);

            final double pw = XmlHelpers.getDouble(elem, "pixelWidth");
            final double ph = XmlHelpers.getDouble(elem, "pixelHeight");
            final double pd = XmlHelpers.getDouble(elem, "pixelDepth");
            final VoxelDimensions voxelSize = new FinalVoxelDimensions("px", pw, ph, pd);

            final ViewSetup setup = new ViewSetup(id, null, size, voxelSize, channel, angle, illumination);
            setups.put(id, setup);
        }
        return setups;
    }
}
