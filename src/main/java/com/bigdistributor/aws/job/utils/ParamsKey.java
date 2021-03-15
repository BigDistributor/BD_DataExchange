package com.bigdistributor.aws.job.utils;

public enum ParamsKey {

    CREDENTIAL_PUBLIC_KEY("publicKey"),
    CREDENTIAL_PRIVATE_KEY("privateKey"),
    TASK(""),
    JOB_ID("jobid"),
    OUTPUT("output"),
    INPUT("input"),
    METADATA("metadata"),
    BUCKET_NAME("bucket"),
    TASK_PARAMS("param");

    private final String key;

    ParamsKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return " --" + key + "=";
    }
}
