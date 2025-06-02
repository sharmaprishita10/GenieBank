package com.example.apiGateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.apiGateway.config.FeignConfig;

@FeignClient(name = "auth-service", url = "http://localhost:8082/auth", configuration=FeignConfig.class)
public interface AuthFeignClient {
	
  @GetMapping("/validate")
  boolean validateToken(@RequestParam("user") String username,
                        @RequestParam("token") String token);
}