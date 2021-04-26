package com.bigdistributor.aws;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class SerializationTester extends TestCase {

    private final Serializable obj;

    public SerializationTester(Serializable obj) {
        super(obj.getClass().getName());
        this.obj = obj;
    }

    @Test
    public void testAll() throws CloneNotSupportedException, IOException, ClassNotFoundException {
        testInstance();
        testConsistency();
        show();
    }

    @Test
    private void testConsistency() throws IOException, CloneNotSupportedException, ClassNotFoundException {
        byte[] serialized1 = serialize(obj);
        byte[] serialized2 = serialize(obj);

        Object deserialized1 = deserialize(serialized1);
        Object deserialized2 = deserialize(serialized2);
        Assert.assertEquals(deserialized1.toString(), deserialized2.toString());
        Assert.assertEquals(obj.toString(), deserialized1.toString());
        Assert.assertEquals(obj.toString(), deserialized2.toString());
    }

    @Test
    private void testInstance() throws IOException, ClassNotFoundException {
        byte[] serialized = serialize(obj);
        Object deserialized = deserialize(serialized);

        Assert.assertEquals(obj.toString(), deserialized.toString());
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    @Test
    public void show() throws IOException, ClassNotFoundException {
        byte[] serialized = serialize(obj);
        Object deserialized = deserialize(serialized);
        System.out.println(deserialized.toString());
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
}