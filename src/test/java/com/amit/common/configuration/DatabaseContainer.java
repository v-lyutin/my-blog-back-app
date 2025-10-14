package com.amit.common.configuration;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class DatabaseContainer {

    private static final PostgreSQLContainer<?> INSTANCE = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6-alpine3.22"));

    static {
        INSTANCE.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }

    private DatabaseContainer() {
        throw new UnsupportedOperationException();
    }

}
