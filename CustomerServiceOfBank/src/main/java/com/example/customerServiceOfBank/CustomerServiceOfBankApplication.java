package com.example.customerServiceOfBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CustomerServiceOfBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceOfBankApplication.class, args);
	}

}