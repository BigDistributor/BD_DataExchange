package com.bigdistributor.aws.dataexchange.aws.s3.func.read;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialsReader;
import com.bigdistributor.aws.dataexchange.aws.s3.func.bucket.S3BucketInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;

import java.io.IOException;

public class AWSCSVReader {


    public static void main(String[] args) throws IllegalAccessException, IOException {
        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);

        S3BucketInstance.init(AWSCredentialInstance.get(), Regions.EU_CENTRAL_1, "demo-stephan-marwan");

//        Document xml = new AWSXMLReader(S3BucketInstance.get(), "big/").read();

        String text = new AWSReader(S3BucketInstance.get(), "", "bigdistributer.csv").get();
        System.out.println(text.split("\n")[0]);
        AWSCredentials cred = AWSCredentialsReader.getFromText(text);
        System.out.println(cred.getAWSAccessKeyId());
        System.out.println(cred.getAWSSecretKey());
        AWSCredentialInstance.initFromText(text);

    }
}
