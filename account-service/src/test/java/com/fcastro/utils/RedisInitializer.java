package com.fcastro.utils;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.atomic.AtomicBoolean;

public class RedisInitializer implements BeforeAllCallback {

    private static final AtomicBoolean INITIAL_INVOCATION = new AtomicBoolean(Boolean.TRUE);

    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = RandomString.make(10);
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis/redis-stack-server:latest");

    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT)
            .withCommand("redis-server", "--requirepass", REDIS_PASSWORD);

    @Override
    public void beforeAll(final ExtensionContext context) {
        if (INITIAL_INVOCATION.getAndSet(Boolean.FALSE)) {
            REDIS_CONTAINER.start();
            addProperties();
        }
    }

    private void addProperties() {
        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(REDIS_PORT)));
        System.setProperty("spring.data.redis.password", REDIS_PASSWORD);
    }
}
