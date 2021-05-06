package com.bigdistributor.aws.ng;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class NeuroglancerDecodeURL {
    private static final String urlExample = "https://neuroglancer-demo.appspot.com/#!%7B%22dimensions%22:%7B%22x%22:%5B4.5e-10%2C%22m%22%5D%2C%22y%22:%5B4.5e-10%2C%22m%22%5D%2C%22z%22:%5B2e-9%2C%22m%22%5D%7D%2C%22position%22:%5B260.9415283203125%2C294.1377258300781%2C43.560123443603516%5D%2C%22crossSectionScale%22:1%2C%22projectionScale%22:512%2C%22layers%22:%5B%7B%22type%22:%22image%22%2C%22source%22:%22n5://https://mzouink-test.s3.eu-central-1.amazonaws.com/dataset.n5/setup0/timepoint0/s0%22%2C%22tab%22:%22source%22%2C%22shaderControls%22:%7B%22normalized%22:%7B%22range%22:%5B0%2C240%5D%2C%22window%22:%5B0%2C240%5D%7D%7D%2C%22name%22:%22new%20layer%22%7D%5D%2C%22selectedLayer%22:%7B%22layer%22:%22new%20layer%22%2C%22visible%22:true%7D%2C%22layout%22:%224panel%22%7D";

    public static void main(String[] args) throws UnsupportedEncodingException {
        String result = URLDecoder.decode(urlExample, "UTF-8");
        System.out.println(result);
    }
}
