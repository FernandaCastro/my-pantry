package com.fcastro.pantryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.app.config", "com.fcastro.kafka", "com.fcastro.security", "com.fcastro.pantryservice"}
)
public class PantryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PantryServiceApplication.class, args);
    }
}
