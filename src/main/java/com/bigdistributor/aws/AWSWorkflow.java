package com.bigdistributor.aws;

import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AWSWorkflow {
    private static AWSWorkflow instance;
    private File tempDir;
    private String credentialsKeyPath = "";
    private String bucketName = "";
    private String localJar = "";
    private String clusterJar = "";
    private String clusterData = "";
    List<String> localData = new ArrayList<>();
    private String metadataPath = "";
    private String clusterMetada = "";

    private AWSWorkflow() {
        tempDir = Files.createTempDir();
    }

    public static AWSWorkflow get() {
        if (instance == null) {
            instance = new AWSWorkflow();
        }
        return instance;
    }

    public void setCredentialsKeyPath(String credentialsKeyPath) {
        this.credentialsKeyPath = credentialsKeyPath;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getCredentialsKeyPath() {
        return credentialsKeyPath;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setLocalJar(String localJar) {
        this.localJar = localJar;
    }

    public String getLocalJar() {
        return localJar;
    }

    public void setClusterJar(String clusterJar) {
        this.clusterJar = clusterJar;
    }

    public String getClusterJar() {
        return clusterJar;
    }

    public void setLocalData(List<String> files) {
        localData = files;
    }

    public List<String> getLocalData() {
        return localData;
    }

    public void setClusterData(String file) {
        this.clusterData = file;
    }

    public String getClusterData() {
        return clusterData;
    }

    public String setFile(String filename) {
        return new File(tempDir, filename).getAbsolutePath();
    }

    public void setMetadataPath(String metadataPath) {
        this.metadataPath = metadataPath;
    }

    public String getMetadataPath() {
        return metadataPath;
    }

    public void setClusterMetadata(String name) {
        this.clusterMetada = name;
    }

    public String getClusterMetada() {
        return clusterMetada;
    }

    public String getFullPathClusterJar() {
        StringBuffer builder = new StringBuffer();
        builder.append("s3://");
        builder.append(bucketName.replace("/", ""));
        builder.append("/");
        builder.append(clusterJar);
        return builder.toString();
    }
}
