package com.fcastro.pantryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.app.config", "com.fcastro.kafka", "com.fcastro.security", "com.fcastro.pantryservice"},
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class PantryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PantryServiceApplication.class, args);
    }
}
