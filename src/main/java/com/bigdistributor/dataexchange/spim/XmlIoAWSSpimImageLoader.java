package com.bigdistributor.dataexchange.spim;

import mpicbg.spim.data.XmlHelpers;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.ImgLoaderIo;
import mpicbg.spim.data.generic.sequence.XmlIoBasicImgLoader;
import org.jdom2.Element;

import java.io.File;

import static mpicbg.spim.data.XmlHelpers.loadPath;
import static mpicbg.spim.data.XmlKeys.IMGLOADER_FORMAT_ATTRIBUTE_NAME;

@ImgLoaderIo( format = "bdv.aws", type = AWSSpimImageLoader.class )
public class XmlIoAWSSpimImageLoader implements XmlIoBasicImgLoader< AWSSpimImageLoader >{

    @Override
    public Element toXml(final AWSSpimImageLoader imgLoader, final File basePath )
    {
        final Element elem = new Element( "ImageLoader" );
        elem.setAttribute( IMGLOADER_FORMAT_ATTRIBUTE_NAME, "bdv.aws" );
        elem.setAttribute( "version", "1.0" );
        elem.addContent( XmlHelpers.pathElement( "aws", imgLoader.getN5File(), basePath ) );
        return elem;
    }


    @Override
    public AWSSpimImageLoader fromXml( final Element elem, final File basePath, final AbstractSequenceDescription< ?, ?, ? > sequenceDescription )
    {
//		final String version = elem.getAttributeValue( "version" );
        final File path = loadPath( elem, "aws", basePath );
        return new AWSSpimImageLoader( path, sequenceDescription );
    }
}