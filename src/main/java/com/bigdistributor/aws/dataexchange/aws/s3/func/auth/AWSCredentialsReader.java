package com.bigdistributor.aws.dataexchange.aws.s3.func.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;
import com.bigdistributor.aws.dataexchange.utils.readfile.CSVReader;

import java.util.List;
import java.util.Map;

public class AWSCredentialsReader extends CSVReader {
    private static final String ACCESS_KEY_ID = "Access key ID";
    private static final String SECRET_ACCESS_KEY = "Secret access key";

    public AWSCredentialsReader(String path) {
        super(path);
    }

    public static AWSCredentials getFromText(String text){
        Map<String, List<String>> values = readText(text);
        AWSCredentials credentials = new BasicAWSCredentials(
                values.get(ACCESS_KEY_ID).get(0),
                values.get(SECRET_ACCESS_KEY).get(0)
        );
        return credentials;
    }

    public AWSCredentials getCredentials() {
        Map<String, List<String>> values = super.read();

        AWSCredentials credentials = new BasicAWSCredentials(
                values.get(ACCESS_KEY_ID).get(0),
                values.get(SECRET_ACCESS_KEY).get(0)
        );
        return credentials;
    }

    public static void main(String[] args) {
        final String csv_path = AWS_DEFAULT.AWS_CREDENTIALS_PATH;
        AWSCredentialsReader reader = new AWSCredentialsReader(csv_path);
        AWSCredentials cred = reader.getCredentials();
        System.out.println(cred.getAWSAccessKeyId()+" - "+cred.getAWSSecretKey());
    }
}
