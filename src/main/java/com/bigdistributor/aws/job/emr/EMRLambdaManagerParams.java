package com.bigdistributor.aws.job.emr;

import com.bigdistributor.aws.job.JarParams;
import com.google.gson.Gson;

public class EMRLambdaManagerParams {
    String task;
    String name;
    String params;
    int instances;

    public EMRLambdaManagerParams(String task, String name, JarParams params, int instances) {
        this.task = task;
        this.name = name;
        this.params = params.toString();
        this.instances = instances;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static void main(String[] args) {
        EMRLambdaManagerParams params = new EMRLambdaManagerParams("task", "name", new JarParams("fusion", "testJobId", "mzouink-test", "dataset-n5.xml", "new_output.n5", "metadata.xml", ""), 3);
        System.out.println(params);
    }

}
