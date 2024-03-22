package com.fcastro.accountService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
				"com.fcastro.accountService",
				"com.fcastro.security.auth",
				"com.fcastro.security.exception",
				"com.fcastro.security.jwt",
				"com.fcastro.security.model",
				"com.fcastro.security.webConfig"
		}
)
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
