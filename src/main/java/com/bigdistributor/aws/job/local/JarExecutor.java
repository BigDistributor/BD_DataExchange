package com.bigdistributor.aws.job.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JarExecutor {

    private final LocalTaskParams params ;

    public JarExecutor(LocalTaskParams params) {
        this.params = params;
    }

    public void run(){
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("java -jar "+params.getTaskFile()+" "+params.getJarParams().toString());

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

// Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

// Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
