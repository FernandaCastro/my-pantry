package com.fcastro.purchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.kafka", "com.fcastro.purchase"}
)
public class PurchaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseServiceApplication.class, args);
    }
}
