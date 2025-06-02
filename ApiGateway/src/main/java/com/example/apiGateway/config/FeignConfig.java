package com.example.apiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  @Bean
  public feign.codec.Decoder feignDecoder() {
    return new feign.jackson.JacksonDecoder();
  }
}