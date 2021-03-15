//package com.bigdistributor.aws.job.aws.emr;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
//import com.amazonaws.services.elasticmapreduce.model.*;
//import com.amazonaws.services.elasticmapreduce.util.StepFactory;
//import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
//import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;
//
//public class RunEmr {
//
//    public static void main(String[] args) throws IllegalAccessException {
//        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);
//
//        AWSCredentials credentials_profile = AWSCredentialInstance.get();
//
//        // create an EMR client using the credentials and region specified in order to create the cluster
//        AmazonElasticMapReduce emr = AmazonElasticMapReduceClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials_profile))
//                .withRegion(Regions.US_WEST_1)
//                .build();
//
//        // create a step to enable debugging in the AWS Management Console
//        StepFactory stepFactory = new StepFactory();
//        StepConfig enabledebugging = new StepConfig()
//                .withName("Enable debugging")
//                .withActionOnFailure("TERMINATE_JOB_FLOW")
//                .withHadoopJarStep(stepFactory.newEnableDebuggingStep());
//
//
////        StepConfig runjar = new StepConfig()
////                .withName("Run jar")
////                .withActionOnFailure("TERMINATE_JOB_FLOW")
////                .withHadoopJarStep(new StepFactory()
////                        .newScriptRunnerStep(
////                                "s3://mzouink-test/bigdistributor_tasks-0.2-SNAPSHOT-jar-with-dependencies.jar",
////                                "--output=s3://mzouink-test/new_output.n5/ --input=s3://mzouink-test/dataset-n5.xml --jobid=hellohello --meta=s3://mzouink-test/metadata.xml"));
////           // specify applications to be installed and configured when EMR creates the cluster
//
//        Application spark = new Application().withName("Spark");
//
//        // create the cluster
//        RunJobFlowRequest request = new RunJobFlowRequest()
//                .withName("My custom with jar_new")
//                .withReleaseLabel("emr-5.20.0") // specifies the EMR release version label, we recommend the latest release
//                .withSteps(enabledebugging)
//                .withApplications( spark)
//                .withLogUri("s3://aws-logs-547527832344-us-east-1/elasticmapreduce/") // a URI in S3 for log files is required when debugging is enabled
//                .withServiceRole("EMR_DefaultRole") // replace the default with a custom IAM service role if one is used
//                .withJobFlowRole("EMR_EC2_DefaultRole") // replace the default with a custom EMR role for the EC2 instance profile if one is used
//                .withInstances(new JobFlowInstancesConfig()
//                        .withEc2SubnetId("subnet-4dade943")
//                        .withEc2KeyName("myEc2Key")
//                        .withInstanceCount(3)
//                        .withKeepJobFlowAliveWhenNoSteps(false)
//                        .withMasterInstanceType("m4.large")
//                        .withSlaveInstanceType("m4.large"));
//
//        RunJobFlowResult result = emr.runJobFlow(request);
//        System.out.println("The cluster ID is " + result.toString());
//
//    }
//
//}