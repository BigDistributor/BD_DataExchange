package com.bigdistributor.aws.job.utils;

import java.io.*;

public class LogAccumulator implements Serializable {
    private final String file;

    public LogAccumulator(String file) {
        this.file = file;
    }

    synchronized public void append(String str) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
            output.append(str);
            output.append(System.lineSeparator());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
