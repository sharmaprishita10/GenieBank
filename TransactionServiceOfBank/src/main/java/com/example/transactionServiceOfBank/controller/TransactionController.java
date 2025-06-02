package com.example.transactionServiceOfBank.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactionServiceOfBank.dto.StatementEntry;
import com.example.transactionServiceOfBank.model.Transaction;
import com.example.transactionServiceOfBank.payload.response.ApiResponse;
import com.example.transactionServiceOfBank.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/add-transaction")
	public ResponseEntity<ApiResponse> addTransaction(@RequestBody Transaction transaction)
	{
		ApiResponse response;
		try {
			
			transactionService.addTransaction(transaction);
			response = new ApiResponse("Transaction created successfully!", HttpStatus.CREATED.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{accountId}/statement")
	public ResponseEntity<ApiResponse> getStatement(@PathVariable int accountId,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
	{
		ApiResponse response;
		try {
			
			// Converting Date to DateTime
		    Timestamp start = Timestamp.valueOf(from.atStartOfDay());
		    Timestamp end   = Timestamp.valueOf(to.plusDays(1).atStartOfDay().minusNanos(1));
		    List<StatementEntry> statement = transactionService.getStatement(accountId, start, end);
			response = new ApiResponse("Statement fetched successfully!", statement, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
