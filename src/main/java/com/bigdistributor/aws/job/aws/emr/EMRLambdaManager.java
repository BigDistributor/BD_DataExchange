package com.bigdistributor.aws.job.aws.emr;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.utils.AWS_DEFAULT;
import com.bigdistributor.aws.job.utils.JarParams;

import java.nio.charset.StandardCharsets;

public class EMRLambdaManager {
private final static String LAMBDA_FUNCTION_NAME = "EMRDistributorManager";
    final AWSCredentials credentials;
    final EMRLambdaManagerParams params;

    private String ans;

    public EMRLambdaManager(AWSCredentials credentials, EMRLambdaManagerParams params) {
        this.credentials = credentials;
        this.params = params;
    }

    public InvokeResult invoke(){
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(LAMBDA_FUNCTION_NAME)
                .withPayload(params.toString());
        InvokeResult invokeResult = null;

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.US_EAST_1).build();

            invokeResult = awsLambda.invoke(invokeRequest);

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

            this.ans = ans;
            System.out.println(ans);

        } catch (ServiceException e) {
            System.out.println(e);
        }

        return invokeResult;
    }

    public static void main(String[] args) throws IllegalAccessException {
        EMRLambdaManagerParams params = new EMRLambdaManagerParams("s3://mzouink-test/bigdistributor_tasks-0.2-SNAPSHOT-jar-with-dependencies.jar","helloFromJava",new JarParams("fusion","testJobId","dataset-n5.xml","new_output.n5","metadata.xml","",AWSCredentialInstance.get()),3);
        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);

        AWSCredentials credentials_profile = AWSCredentialInstance.get();

        InvokeResult result = new EMRLambdaManager(credentials_profile, params).invoke();
        System.out.println(result.getStatusCode());
        System.out.println(result.getFunctionError());

    }
}
