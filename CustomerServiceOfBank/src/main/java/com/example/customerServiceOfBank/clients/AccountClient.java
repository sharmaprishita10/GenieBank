package com.example.customerServiceOfBank.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.customerServiceOfBank.payload.request.UpdateBalanceRequest;
import com.example.customerServiceOfBank.payload.response.ApiResponse;


@FeignClient(name = "account-service", url  = "${account.service.url}") 
public interface AccountClient {

	@PostMapping("/get-id-by-accNum")
	ApiResponse getIdByAccNum(@RequestBody String accNum);
	
	@PutMapping("/update-balance")
	ApiResponse updateBalance(@RequestBody UpdateBalanceRequest updateBalRequest);
	
	@GetMapping("/get-balance/{id}")
	ApiResponse getBalance(@PathVariable int id);
	
	@GetMapping("/get-id-by-custId/{customerId}")
	ApiResponse getIdByCustId(@PathVariable int customerId);
}
