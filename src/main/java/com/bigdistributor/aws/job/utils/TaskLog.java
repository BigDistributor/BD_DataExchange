package com.bigdistributor.aws.job.utils;

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskLog {
    final Integer id;
    Map<TimeLabel,Long> logs = new HashMap<>();

    public TaskLog(int id) {
        this.id = id;
    }

    public void setStatus(TimeLabel label){
        Date date= new Date();
        long time = date.getTime();
        logs.put(label,time);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static void main(String[] args) throws InterruptedException {
        TaskLog log = new TaskLog(2);
        log.setStatus(TimeLabel.TaskStarted);
        Thread.sleep(1000);
        log.setStatus(TimeLabel.DataLoaded);
        System.out.println(log.toString());

        LogAccumulator accumulator  = new LogAccumulator("/Users/Marwan/Desktop/log.txt");

        accumulator.append(log.toString());
        accumulator.append(log.toString());
    }
}
