package com.fcastro.pantry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.kafka", "com.fcastro.pantry"}
)
public class PantryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PantryServiceApplication.class, args);
    }
}
