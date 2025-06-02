package com.example.adminServiceOfBank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.adminServiceOfBank.payload.request.*;
import com.example.adminServiceOfBank.payload.response.*;

@FeignClient(name = "customer-service", url  = "${customer.service.url}") 
public interface CustomerClient {

	@PostMapping("/add-customer")
	ApiResponse addCustomer(@RequestBody NewCustomerRequest custDto);

	@GetMapping("/get-id-by-custId/{custId}")
	ApiResponse getIdByCustId(@PathVariable String custId);
	
	@GetMapping("/get-customer-by-custId/{custId}")
	ApiResponse getByCustId(@PathVariable String custId);
}
