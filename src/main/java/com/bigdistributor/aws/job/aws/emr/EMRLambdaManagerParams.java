package com.bigdistributor.aws.job.aws.emr;

import com.bigdistributor.aws.job.utils.JarParams;
import com.google.gson.Gson;

public class EMRLambdaManagerParams {
    String task;
    String name;
    String params;
    String type;
    int instances;

    public EMRLambdaManagerParams(String task, String name, JarParams params, int instances, InstancesType type) {
        this.task = task;
        this.name = name;
        this.params = params.toString();
        this.instances = instances;
    }

    public EMRLambdaManagerParams(String task, String name, JarParams params, int instances) {
       this(task,name,params,instances,InstancesType.ON_DEMAND);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
