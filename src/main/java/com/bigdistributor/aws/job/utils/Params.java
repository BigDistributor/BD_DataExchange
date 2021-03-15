package com.bigdistributor.aws.job.utils;

import com.amazonaws.regions.Regions;

public class Params {
    private static Params instance;
    private final String credentialPath, bucketName, path, xmlFile;
    private Regions region =  Regions.EU_CENTRAL_1;

    private Params(String credentialPath, String bucketName, String path, String xmlFile) {
        this.credentialPath = credentialPath;
        this.bucketName = bucketName;
        this.path = path;
        this.xmlFile = xmlFile;
    }

    public static Params init(String credentialPath, String bucketName, String path, String xmlFile) {
        instance = new Params(credentialPath, bucketName, path, xmlFile);
        return instance;
    }

    public static Params get() {
        return instance;
    }

    public String getCredentialPath() {
        return credentialPath;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getPath() {
        return path;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public Regions getRegion() {
        return region;
    }
}
