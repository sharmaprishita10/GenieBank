package com.example.adminServiceOfBank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.adminServiceOfBank.payload.request.*;
import com.example.adminServiceOfBank.payload.response.*;

@FeignClient(name = "authentication-service", url  = "${auth.service.url}") 
public interface AuthenticationClient {

	@PostMapping("/add-user")
	ApiResponse addNewUser(@RequestBody NewUserRequest userDto);

}