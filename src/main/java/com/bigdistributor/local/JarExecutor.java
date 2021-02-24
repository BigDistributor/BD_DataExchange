package com.bigdistributor.local;

import java.io.IOException;
import java.io.InputStream;

public class JarExecutor {

    private final LocalTaskParams params ;

    public JarExecutor(LocalTaskParams params) {
        this.params = params;
    }

    public void run(){
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("java -jar "+params.getTaskFile()+" "+params.getJarParams().toString());
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
