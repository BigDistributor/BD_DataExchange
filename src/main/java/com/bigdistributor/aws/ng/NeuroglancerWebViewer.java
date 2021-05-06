package com.bigdistributor.aws.ng;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NeuroglancerWebViewer {
    private static final String baseUrl = "https://neuroglancer-demo.appspot.com/#!";

    private final NeuroglancerInput input;

    public NeuroglancerWebViewer(NeuroglancerInput input) {
        this.input = input;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        NeuroglancerInput input = new NeuroglancerInput("s3://mzouink-test/dataset.n5", "setup0/timepoint0/s0");

        String url = input.convertS3UriToHTML();
        String json = input.getFullJson();
        System.out.println(url);
        System.out.println(json);

        new NeuroglancerWebViewer(input).openInBrowser();

    }

    public void openInBrowser() throws URISyntaxException, IOException {
        String fullUrl = baseUrl + input.getFullJson();
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(fullUrl));
        }
    }
}
