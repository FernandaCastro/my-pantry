package com.fcastro.mypantry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.fcastro")
@EnableJpaRepositories("com.fcastro")
@EntityScan("com.fcastro")
public class MyPantryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPantryApplication.class, args);
    }
}
