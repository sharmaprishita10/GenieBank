package com.example.adminServiceOfBank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sendgrid.SendGrid;

@Configuration
public class SendGridConfig {

	@Bean
	public SendGrid sendGrid(@Value("${SENDGRID_API_KEY}") String apiKey) {
		return new SendGrid(apiKey);
	}
}
