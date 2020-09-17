package com.bigdistributer.dataexchange.utils.log;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log {

    private static final Logger logger = Logger.getLogger(Log.class);

    static {
        logger.setLevel(Level.ALL);
    }

    public static void info(String string) {
        System.out.println(string);
        if (logger.isInfoEnabled()) {
            logger.info(string);
        }
    }

    public static void debug(String string) {
        System.out.println(string);
        if (logger.isDebugEnabled()) {
            logger.debug(string);
        }
    }

    public static void error(String string) {
        System.out.println(string);
        logger.error(string);
    }

    public static void main(String[] args) {
        System.out.println("Hello");
        Log.info("Hello");
    }
}
