package com.bigdistributor.aws.job;

public class JarParams {
    String task;
    String jobid;
    String output;
    String input;
    String metada;
    String bucketname;
    String params;

    public JarParams(String task, String jobid, String bucketname, String input, String output, String metada, String params) {
        this.task = task;
        this.jobid = jobid;
        this.output = output;
        this.input = input;
        this.metada = metada;
        this.bucketname = bucketname;
        this.params = params;
    }

    //"--output=s3://mzouink-test/new_output.n5/ --input=s3://mzouink-test/dataset-n5.xml --jobid=hellohello --meta=s3://mzouink-test/metadata.xml"
    @Override
    public String toString() {
        return task+
                " --jobid=" + jobid
                + " --bucket=" + bucketname
                + " --input=" + input
                + " --output=" + output
                + " --metadata=" + metada
                + " --params=" + params;
    }
}
