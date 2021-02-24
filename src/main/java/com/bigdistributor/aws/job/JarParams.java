package com.bigdistributor.aws.job;

import com.amazonaws.auth.AWSCredentials;

public class JarParams {
    private final AWSCredentials cred;
    private final String task;
    private final String jobid;
    private final String output;
    private final String input;
    private final String metadata;
    private final String bucketname;
    private final String params;

    public JarParams(String task, String jobid, String bucketname, String input, String output, String metadata, String params, AWSCredentials cred) {
        this.task = task;
        this.jobid = jobid;
        this.output = output;
        this.input = input;
        this.metadata = metadata;
        this.bucketname = bucketname;
        this.params = params;
        this.cred = cred;
    }

    @Override
    public String toString() {
        return task +
                ParamsKey.JOB_ID.toString() + jobid
                + ParamsKey.BUCKET_NAME.toString() + bucketname
                + ParamsKey.INPUT.toString() + input
                + ParamsKey.OUTPUT.toString() + output
                + ParamsKey.METADATA.toString() + metadata
                + ((params == null || params.trim().isEmpty()) ? "" : ParamsKey.TASK_PARAMS.toString() + params)
                + ((cred == null) ? "" : (ParamsKey.CREDENTIAL_PUBLIC_KEY.toString() + cred.getAWSAccessKeyId() + ParamsKey.CREDENTIAL_PRIVATE_KEY.toString() + cred.getAWSSecretKey()));
    }
}
