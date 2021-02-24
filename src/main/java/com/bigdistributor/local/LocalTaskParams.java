package com.bigdistributor.local;

import com.bigdistributor.aws.job.JarParams;

public class LocalTaskParams {
    private final String taskFile;
    private final JarParams jarParams;

    public LocalTaskParams(String taskFile, JarParams jarParams) {
        this.taskFile = taskFile;
        this.jarParams = jarParams;
    }

    public String getTaskFile() {
        return taskFile;
    }

    public JarParams getJarParams() {
        return jarParams;
    }
}
