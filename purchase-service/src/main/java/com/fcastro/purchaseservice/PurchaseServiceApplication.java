package com.fcastro.purchaseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.app.config", "com.fcastro.kafka", "com.fcastro.security", "com.fcastro.purchaseservice"}
)
//@EnableJpaRepositories({"com.fcastro.security", "com.fcastro.purchaseservice"})
//@EntityScan({"com.fcastro.security", "com.fcastro.purchaseservice"})
public class PurchaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseServiceApplication.class, args);
    }
}
