package com.bigdistributor.biglogger.handlers.mq.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.bigdistributor.aws.dataexchange.aws.s3.func.auth.AWSCredentialInstance;
import com.bigdistributor.biglogger.adapters.Log;
import com.bigdistributor.biglogger.generic.LogHandler;
import com.bigdistributor.biglogger.generic.LogMode;
import com.bigdistributor.biglogger.generic.LogReceiver;
import com.bigdistributor.core.app.ApplicationMode;
import com.bigdistributor.core.remote.mq.MQLogReceiveDispatchManager;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//

@LogHandler(format = "SNS", type = LogMode.Advance, modes = {ApplicationMode.DistributionMaster, ApplicationMode.DistributionMasterFiji})
public class SNSLogReceiverHandler implements LogReceiver {
    private static final String QUEUE_URL = "https://sqs.eu-central-1.amazonaws.com/547527832344/bigdistributor";
    private static final Log logger = Log.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private ExecutorService executorService;
    private AmazonSQS sqs;
    private boolean shutdown;

    @Override
    public void start() {
        init();
        if (executorService == null)
            this.executorService = Executors.newFixedThreadPool(1);
        else
            executorService.shutdownNow();
        Runnable runnable = setRunnable();
        executorService.execute(runnable);
        logger.info("MQ Receiver started .. ");
    }

    private Runnable setRunnable() {
        return () -> {
            try {
                logger.info(" [*] Waiting for messages");

                while (!shutdown){

                    Thread.sleep(3000);
                    List<Message> messages = sqs.receiveMessage(QUEUE_URL).getMessages();
                    System.out.println(messages.size());
                    for (Message m : messages) {
                        logger.info("Message Received: " + m.getMessageId() + "-" + m.getBody());
                        MQLogReceiveDispatchManager.addLog(m.getBody());
                    }
                }
                logger.error("SNS Shutdown");
            } catch (InterruptedException e) {
                logger.error(e.toString());
                restart();
            }
        };
    }

    private void restart() {
        executorService.shutdownNow();
        Runnable runnable = setRunnable();
        executorService.execute(runnable);
    }

    private void init() {
        shutdown = false;
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentialInstance.get()))
                .build();
    }

    @Override
    public void stop() {
        shutdown = true;
    }
}
