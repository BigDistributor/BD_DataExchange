package com.bigdistributor.biglogger.handlers.mq.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.aws.dataexchange.utils.AWS_DEFAULT;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.biglogger.generic.LogHandler;
import com.bigdistributor.biglogger.generic.LogMode;
import com.bigdistributor.core.app.ApplicationMode;
import com.bigdistributor.core.remote.mq.entities.MQMessage;
import com.bigdistributor.core.remote.mq.entities.MQTopic;
import com.bigdistributor.core.task.JobID;

import java.lang.invoke.MethodHandles;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@LogHandler(format = "SNS", type = LogMode.Advance, modes = {ApplicationMode.ExecutionNode})
public class SNSLogPublishHandler extends Handler {

    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/547527832344/bigdistributor";
    private static AmazonSQS sqs;
    private static String jobId;

    private static void init(){
        logger.info("Init AWS SNS Log Handler Initialization..");

        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialInstance.get()))
                .build();
        jobId = JobID.get();
    }

    public static void main(String[] args) throws IllegalAccessException {
        AWSCredentialInstance.init(AWS_DEFAULT.AWS_CREDENTIALS_PATH);
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialInstance.get()))
                .build();

        //One message
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(QUEUE_URL)
                .withMessageBody("hello world111");
        sqs.sendMessage(send_msg_request);

        // all message
//        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
//                .withQueueUrl(QUEUE_URL)
//                .withEntries(
//                        new SendMessageBatchRequestEntry(
//                                "msg_1", "Hello from message 1"),
//                        new SendMessageBatchRequestEntry(
//                                "msg_2", "Hello from message 2")
//                                .withDelaySeconds(10));
//        sqs.sendMessageBatch(send_batch_request);
    }

    @Override
    public void publish(LogRecord record) {
        if(sqs!=null){
            String message;
            if (record.getLevel() == Level.WARNING) {
                message = record.getMessage();
            } else {
                message = new MQMessage(MQTopic.LOG, jobId, 0, record.getMessage()).toString();
            }

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(QUEUE_URL)
                    .withMessageBody(message);
            sqs.sendMessage(send_msg_request);
        }
        else{
            init();
        }

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
