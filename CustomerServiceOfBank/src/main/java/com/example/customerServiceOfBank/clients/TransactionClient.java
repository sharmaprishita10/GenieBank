package com.example.customerServiceOfBank.clients;

import java.time.LocalDate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.customerServiceOfBank.payload.request.TransactionRequest;
import com.example.customerServiceOfBank.payload.response.ApiResponse;

@FeignClient(name = "transaction-service", url  = "${transaction.service.url}") 
public interface TransactionClient {

	@PostMapping("/add-transaction")
	ApiResponse addTransaction(@RequestBody TransactionRequest transactionRequest);
	
	@GetMapping("/{accountId}/statement")
	ApiResponse getStatement(@PathVariable int accountId,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to);
}
