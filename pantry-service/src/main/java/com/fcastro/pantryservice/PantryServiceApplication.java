package com.fcastro.pantryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {"com.fcastro.kafka", "com.fcastro.security", "com.fcastro.pantryservice"}
)
@EnableJpaRepositories({"com.fcastro.security", "com.fcastro.pantryservice"})
@EntityScan({"com.fcastro.security", "com.fcastro.pantryservice"})
public class PantryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PantryServiceApplication.class, args);
    }
}
