package com.fcastro.pantryService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fcastro"})
public class PantryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PantryServiceApplication.class, args);
    }
}
