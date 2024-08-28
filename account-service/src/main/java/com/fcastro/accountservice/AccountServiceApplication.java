package com.fcastro.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
		scanBasePackages = {"com.fcastro.app.config", "com.fcastro.security.core", "com.fcastro.accountservice"}
)
@EnableCaching
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
