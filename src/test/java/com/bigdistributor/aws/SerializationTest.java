package com.bigdistributor.aws;

import com.bigdistributor.aws.data.CredentialSupplier;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SerializationTest {

    private CredentialSupplier cred;

    @Before
    public void setUp() {
        cred = new CredentialSupplier("public","private");
    }

    @Test
    public void testSerialization() throws IOException, CloneNotSupportedException, ClassNotFoundException {
    new SerializationTester(cred).testAll();
    }

}