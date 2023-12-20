package com.fcastro.purchase.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class PropertiesServiceIntegrationTest {

    @Autowired
    PropertiesService service;

    @Autowired
    PropertiesRepository repository;

    @Test
    public void whenGet() {
        var property = service.get("product.categories");
        assertThat(property.get().getPropertyValue()).contains("{\"categories\": [\"HORTIFRUTI\"");
    }
}
