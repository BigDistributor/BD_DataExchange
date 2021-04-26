package com.bigdistributor.aws.data;

public class CredentialConfig {
    private transient static CredentialSupplier instance;

    public static CredentialSupplier get() {
        return instance;
    }

    public static void set(CredentialSupplier instance) {
        CredentialConfig.instance = instance;
    }
}
