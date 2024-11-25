package com.fcastro.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
		scanBasePackages = {"com.fcastro.commons", "com.fcastro.security.core", "com.fcastro.kafka", "com.fcastro.accountservice"}
)
@EnableJpaRepositories
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
