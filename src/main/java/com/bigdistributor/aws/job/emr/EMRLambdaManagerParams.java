package com.bigdistributor.aws.job.emr;

import com.google.gson.Gson;

public class EMRLambdaManagerParams {
    String task;
    String name;
    String params;
    int instances;

    public EMRLambdaManagerParams(String task, String name, String params, int instances) {
        this.task = task;
        this.name = name;
        this.params = params;
        this.instances = instances;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static void main(String[] args) {
        EMRLambdaManagerParams params = new EMRLambdaManagerParams("task","name","params",3);
        System.out.println(params);
    }

}
