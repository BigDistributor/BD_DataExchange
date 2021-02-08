package com.bigdistributor.biglogger.handlers.mq.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;

import java.util.List;

public class Get {
    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/547527832344/bigdistributor";


    public static void main(String[] args) throws IllegalAccessException {
        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialInstance.get()))
                .build();
        List<Message> messages = sqs.receiveMessage(QUEUE_URL).getMessages();
        int i = 0;
        System.out.println(messages.size());
        for (Message m : messages)
            System.out.println((i++) + "Message: " + m.getMessageId() + "-" + m.getBody());

    }
}
