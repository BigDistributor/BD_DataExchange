package com.bigdistributor.aws.job.spark;

import org.apache.spark.scheduler.SparkListener;

import java.io.File;

public class CustomSparkListener extends SparkListener {

    private final File logFile;

    public CustomSparkListener(File logFile) {
        this.logFile = logFile;
    }


}
