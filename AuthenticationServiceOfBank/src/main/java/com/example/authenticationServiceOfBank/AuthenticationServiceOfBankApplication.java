package com.example.authenticationServiceOfBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*@ComponentScan(basePackages = {
	    "com.example.authenticationServiceOfBank",      
	    "com.example.sharedJwtLibrary.security"        // the shared JWT library
	})*/

@SpringBootApplication
public class AuthenticationServiceOfBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceOfBankApplication.class, args);
	}

}
