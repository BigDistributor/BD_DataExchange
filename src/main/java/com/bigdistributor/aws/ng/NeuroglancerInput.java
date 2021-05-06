package com.bigdistributor.aws.ng;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NeuroglancerInput {

    private final static String resource = NeuroglancerInput.class.getClassLoader().getResource("NeuroglancerExample.json").getPath();

    private final String s3Uri;
    private final String path;

    public NeuroglancerInput(String s3Uri, String path) {
        this.s3Uri = s3Uri;
        this.path = path;
    }

    public String getFullJson() throws FileNotFoundException, UnsupportedEncodingException {
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        }
        String url = convertS3UriToHTML();
        JsonObject jsonObject = new JsonParser().parse(new FileReader(resource)).getAsJsonObject();
        jsonObject.getAsJsonArray("layers").get(0).getAsJsonObject().addProperty("source", url);
        return URLEncoder.encode(jsonObject.toString(), "UTF-8");
    }

    public String convertS3UriToHTML() {
        String[] urlparts = s3Uri.replace("//", "/").split("/");
        if (urlparts.length < 3)
            throw new IllegalArgumentException("Invalid data s3 uri :" + s3Uri);
        String bucketname = urlparts[1];
        String key = "";
        for (int i = 2; i < urlparts.length; i++)
            key = key + urlparts[i] + "/";

        return "n5://https://" + bucketname + ".s3.amazonaws.com/" + key + path;
    }

}
