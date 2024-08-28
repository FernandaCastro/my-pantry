package com.fcastro.utils;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresInitializer implements BeforeAllCallback {

    private static final AtomicBoolean INITIAL_INVOCATION = new AtomicBoolean(Boolean.TRUE);

    private static final String POSTGRES_USERNAME = "account";
    private static final String POSTGRES_PASSWORD = "account";  //RandomString.make(10);
    private static final String POSTGRES_IMAGE = "postgres:latest";

    private static final PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer(POSTGRES_IMAGE)
            .withDatabaseName("it-db")
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD);

    @Override
    public void beforeAll(final ExtensionContext context) {
        if (INITIAL_INVOCATION.getAndSet(Boolean.FALSE)) {
            POSTGRES_CONTAINER.start();
            addProperties();
        }
    }

    private void addProperties() {
        System.setProperty("spring.datasource.driverClassName", POSTGRES_CONTAINER.getDriverClassName());
        System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES_USERNAME);
        System.setProperty("spring.datasource.password", POSTGRES_PASSWORD);
        System.setProperty("spring.flyway.baselineOnMigrate", String.valueOf(false));
    }
}
