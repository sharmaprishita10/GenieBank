package com.example.accountServiceOfBank.controller;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.accountServiceOfBank.model.Account;
import com.example.accountServiceOfBank.payload.request.UpdateBalanceRequest;
import com.example.accountServiceOfBank.payload.response.ApiResponse;
import com.example.accountServiceOfBank.service.AccountService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/add-account")
	public ResponseEntity<ApiResponse> addAccount(@RequestBody Account account)
	{
		ApiResponse response;
		try {
			
			String newAccountNumber = accountService.addAccount(account);
			response = new ApiResponse("Account added successfully!", newAccountNumber, HttpStatus.CREATED.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);
		} 
		catch(DataIntegrityViolationException e)
		{
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getMostSpecificCause().getMessage(); 
			int index = errorMessage.indexOf(" for");
			String result = (index != -1) ? errorMessage.substring(0, index) : errorMessage;
			
			response = new ApiResponse(result, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		}
		catch(ConstraintViolationException e)
		{
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getConstraintViolations().stream()
					.map(ConstraintViolation::getMessage)   
				    .collect(Collectors.joining("; "));

			response = new ApiResponse(errorMessage, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/get-id-by-accNum")
	public ResponseEntity<ApiResponse> getIdByAccNum(@RequestBody String accNum) {

		ApiResponse response;
		try {
			Account account =  accountService.findIdByAccNum(accNum);
			if(account == null)
			{
				response = new ApiResponse("No such account found.", HttpStatus.OK.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
			}
			response = new ApiResponse("Id Fetch successful.", account.getId(), HttpStatus.OK.value());
			return ResponseEntity.ok(response);
			
		} catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update-balance")
	public ResponseEntity<ApiResponse> updateBalance(@RequestBody UpdateBalanceRequest updateBalRequest) {

		ApiResponse response;
		try {

			accountService.updateBalance(updateBalRequest);
			response = new ApiResponse("Account balance updated successfully.", HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("No such account found.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/get-balance/{id}")
	public ResponseEntity<ApiResponse> getBalance(@PathVariable int id){
		
		ApiResponse response;
		try {

			double balance = accountService.getBalance(id);
			response = new ApiResponse("Account balance fetched successfully.", balance, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("No such account found.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		}
	} 
	
	@GetMapping("/get-id-by-custId/{customerId}")
	public ResponseEntity<ApiResponse> getIdByCustId(@PathVariable int customerId){
		
		ApiResponse response;
		try {

			int accountId = accountService.findIdByCustId(customerId);
			response = new ApiResponse("Id Fetch successful.", accountId, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (NullPointerException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("No account found.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		}
	}
}
