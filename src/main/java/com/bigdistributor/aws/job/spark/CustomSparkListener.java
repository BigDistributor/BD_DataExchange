package com.bigdistributor.aws.job.spark;

import com.bigdistributor.aws.job.utils.LogAccumulator;
import org.apache.spark.scheduler.*;

import java.io.File;
import java.util.Date;

public class CustomSparkListener extends SparkListener {

    private final File logFile;
    private final LogAccumulator accumulator;

    public CustomSparkListener(File logFile) {
        this.logFile = logFile;
        accumulator = new LogAccumulator(logFile.getAbsolutePath());
    }

    @Override
    public void onStageSubmitted(SparkListenerStageSubmitted stageSubmitted) {
        accumulator.append(getString("StageSubmitted",stageSubmitted.stageInfo().stageId()));
    }

    @Override
    public void onStageCompleted(SparkListenerStageCompleted stageCompleted) {
        accumulator.append(getString("StageCompleted",stageCompleted.stageInfo().stageId()));
    }

    @Override
    public void onTaskStart(SparkListenerTaskStart taskStart) {
        accumulator.append(getString("TaskStart",taskStart.taskInfo().taskId()));
    }

    @Override
    public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
        accumulator.append(getString("TaskEnd",taskEnd.taskInfo().taskId()));
    }

    @Override
    public void onJobStart(SparkListenerJobStart jobStart) {
        accumulator.append(getString("JobStart", jobStart.jobId()));
    }

    @Override
    public void onJobEnd(SparkListenerJobEnd jobEnd) {
        accumulator.append(getString("JobEnd",jobEnd.jobId()));
    }

    private String getString(String elm, Object id) {
        Date date= new Date();
        long time = date.getTime();
        return elm+";"+id+";"+time;
    }

}
