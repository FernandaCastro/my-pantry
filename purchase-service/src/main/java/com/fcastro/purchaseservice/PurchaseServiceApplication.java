package com.fcastro.purchaseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.commons", "com.fcastro.kafka", "com.fcastro.security", "com.fcastro.purchaseservice"},
        exclude = UserDetailsServiceAutoConfiguration.class
)

public class PurchaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseServiceApplication.class, args);
    }
}
