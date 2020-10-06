package com.bigdistributer.dataexchange.s3.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class JobID {
    private static String id;

    private static final String nonAcceptableKeys = "_";

    private static String format(String id, String wrongKeys) {
        String result = id.toLowerCase();
        for (char ch : wrongKeys.toCharArray())
            result.replace(Character.toString(ch), "");
        return result;
    }

    private static String id() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String id = UUID.randomUUID().toString().replace("-", "");
        return timeStamp + "-" + id;
    }

    private static void init(String id) {
        JobID.id = id;
        System.out.println("Id: " + id);
    }

    public static String get() {
        if (id == null)
            init(generateID());
        return id;
    }

    private static String generateID() {
        return format(id(), nonAcceptableKeys);
    }

    public static void set(String id) {
        init(id);
    }
}
