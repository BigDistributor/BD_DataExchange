package com.bigdistributor.aws.job;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class CallHTTPGetService {
    private static final String URL = "https://vztd6xbki2.execute-api.us-east-1.amazonaws.com/alpha";

    public static void main(String[] args) throws IOException {

        HttpClient client = HttpClientBuilder.create().build();

        HttpUriRequest httpUriRequest = new HttpPost(URL);

        HttpResponse response = client.execute(httpUriRequest);
        System.out.println(response);

    }
}
