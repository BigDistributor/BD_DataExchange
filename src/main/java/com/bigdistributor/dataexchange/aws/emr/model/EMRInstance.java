package com.bigdistributor.dataexchange.aws.emr.model;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.bigdistributor.dataexchange.aws.s3.model.AWSCredentialInstance;
import com.bigdistributor.dataexchange.job.model.JobID;
import com.bigdistributor.dataexchange.utils.DEFAULT;

public class EMRInstance {
    private final AmazonElasticMapReduce emr;
    private final AWSCredentials credentials;

    public EMRInstance(AWSCredentials credentials, Regions region) {
        this.credentials = credentials;

        // create an EMR client using the credentials and region specified in order to create the cluster
        emr = AmazonElasticMapReduceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    public EMRInstance(AWSCredentials credentials) {
        this(credentials, Regions.EU_CENTRAL_1);
    }

    public RunJobFlowResult runJob(RunJobFlowRequest request) {
        return emr.runJobFlow(request);
    }

    public RunJobFlowRequest createRequest(String s3Path) {

        // create a step to enable debugging in the AWS Management Console
        StepFactory stepFactory = new StepFactory();
        StepConfig enableDebugging = new StepConfig()
                .withName("Enable debugging")
                .withActionOnFailure("TERMINATE_JOB_FLOW")
                .withHadoopJarStep(stepFactory.newEnableDebuggingStep());

        // specify applications to be installed and configured when EMR creates the cluster
//        Application hive = new Application().withName("Hive");
        Application spark = new Application().withName("Spark");
//        Application ganglia = new Application().withName("Ganglia");
//        Application zeppelin = new Application().withName("Zeppelin");

        // create the cluster
        RunJobFlowRequest request = new RunJobFlowRequest()
                .withName("MyClusterCreatedFromJava")
                .withReleaseLabel("emr-5.20.0") // specifies the EMR release version label, we recommend the latest release
                .withSteps(enableDebugging)
                .withApplications( spark)
//                .withApplications(hive, spark, ganglia, zeppelin)
                .withLogUri(s3Path) // a URI in S3 for log files is required when debugging is enabled
                .withServiceRole("EMR_DefaultRole") // replace the default with a custom IAM service role if one is used
                .withJobFlowRole("EMR_EC2_DefaultRole") // replace the default with a custom EMR role for the EC2 instance profile if one is used
                .withInstances(new JobFlowInstancesConfig()
                        .withEc2SubnetId("subnet-12ab34c56")
                        .withEc2KeyName("myEc2Key")
                        .withInstanceCount(3)
                        .withKeepJobFlowAliveWhenNoSteps(true)
                        .withMasterInstanceType("m4.large")
                        .withSlaveInstanceType("m4.large"));
        return request;
    }

    public static void main(String[] args) throws IllegalAccessException {

        final String s3Path = "s3://" + DEFAULT.id;

        JobID.set(DEFAULT.id);
        AWSCredentialInstance.init(DEFAULT.AWS_CREDENTIALS_PATH);
        AWSCredentials credentials = AWSCredentialInstance.get();

        final EMRInstance emr = new EMRInstance(credentials);

        RunJobFlowResult result = emr.runJob(emr.createRequest(s3Path));

        System.out.println("The cluster ID is " + result.toString());
    }
}
