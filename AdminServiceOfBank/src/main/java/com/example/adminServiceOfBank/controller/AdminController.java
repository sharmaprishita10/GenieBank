package com.example.adminServiceOfBank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.adminServiceOfBank.payload.request.*;
import com.example.adminServiceOfBank.payload.response.*;
import com.example.adminServiceOfBank.service.AdminService;
import com.twilio.exception.ApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/register-employee")
	public ResponseEntity<ApiResponse> addEmployee(@RequestBody NewEmpRequest empDto)
	{
		ApiResponse response;
		try {
			
			adminService.addEmployee(empDto);
			response = new ApiResponse("Employee registered and corresponding user added successfully!", HttpStatus.CREATED.value());
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
		catch(NoSuchElementException e)
		{
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("No such branch found.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		}
		catch(ConstraintViolationException e)
		{
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getConstraintViolations().stream()
					.map(ConstraintViolation::getMessage)   
				    .collect(Collectors.joining("; "));

			response = new ApiResponse(errorMessage, HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/send-sms-otp")
	public ResponseEntity<ApiResponse> sendSmsOtp(@RequestBody MobileNumberRequest mobileNumberRequest)
	{
		ApiResponse response;
		try {
			adminService.sendSmsOtp(mobileNumberRequest.getMobileNumber());
			response = new ApiResponse("OTP sent successfully!", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/verify-sms-otp")
	public ResponseEntity<ApiResponse> verifySmsOtp(@RequestBody MobileOtpRequest mobileOtpRequest)
	{
		ApiResponse response;
		try {
			String message = adminService.verifySmsOtp(mobileOtpRequest.getMobileNumber(), mobileOtpRequest.getMobileOtp());
			response = new ApiResponse(message, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(ApiException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Bad Request.", HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/send-email-otp")
	public ResponseEntity<ApiResponse> sendEmailOtp(@RequestBody EmailRequest emailRequest)
	{
		ApiResponse response;
		try {
			adminService.sendEmailOtp(emailRequest.getEmail());
			response = new ApiResponse("OTP sent successfully!", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/verify-email-otp")
	public ResponseEntity<ApiResponse> verifyEmailOtp(@RequestBody EmailOtpRequest emailOtpRequest)
	{
		ApiResponse response;
		try {
			String message = adminService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getEmailOtp());
			response = new ApiResponse(message, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(ApiException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Bad Request.", HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/register-customer")
	public ResponseEntity<ApiResponse> addCustomer(@RequestHeader("X-User") String username, @RequestBody NewCustomerRequest custDto)
	{
		ApiResponse response;
		try {
			String message = adminService.addCustomer(username, custDto);
			response = new ApiResponse(message, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/open-account")
	public ResponseEntity<ApiResponse> openAccount(@RequestHeader("X-User") String username, @RequestBody CustIdRequest custIdRequest)
	{
		ApiResponse response;
		try {
			adminService.openAccount(username, custIdRequest.getCustId());
			response = new ApiResponse("Account opened successfully!", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("deposit")
	public ResponseEntity<ApiResponse> depositAmount(@RequestHeader("X-User") String username, @RequestBody AccountNumRequest accNumRequest)
	{
		ApiResponse response;
		try {
			String data = adminService.depositAmount(username, accNumRequest.getAccountNumber(), accNumRequest.getAmount());
			if(data == null)
			{
				response = new ApiResponse("No such account found.", HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
			}
			response = new ApiResponse("Amount deposited successfully!", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("withdraw")
	public ResponseEntity<ApiResponse> withdrawAmount(@RequestHeader("X-User") String username, @RequestBody AccountNumRequest accNumRequest)
	{
		ApiResponse response;
		try {
			String message = adminService.withdrawAmount(username, accNumRequest.getAccountNumber(), accNumRequest.getAmount());
			if(message == null)
			{
				response = new ApiResponse("No such account found.", HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
			}
			
			response = new ApiResponse(message, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/{custId}/statement")
	public ResponseEntity<ApiResponse> getStatement(@PathVariable String custId,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
	{
		ApiResponse response;
		try {
			
			if (to.isBefore(from)) {
	             
                response = new ApiResponse("End date cannot be before the start date.", HttpStatus.OK.value());
    			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
            }
			
			Object data = adminService.getStatement(custId, from, to);
			if(data == null)
			{
				response = new ApiResponse("No such customer found.", HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
			}
			
			List statements = (List) data;
			if(statements.isEmpty())
			{
				response = new ApiResponse("No records found.", data, HttpStatus.OK.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
			}
			else
			{
				response = new ApiResponse("Statement fetched successfully!", data, HttpStatus.OK.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
			}
			
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/get-balance/{custId}")
	public ResponseEntity<ApiResponse> getStatement(@PathVariable String custId)
	{
		ApiResponse response;
		try {
			Double balance = adminService.getBalance(custId);
			if(balance == null)
			{
				response = new ApiResponse("No such account or customer found.", HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
			}
			
			response = new ApiResponse("Account balance fetched successfully.", "INR " + balance, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} 
		catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/get-customer-by-custId/{custId}")
	public ResponseEntity<ApiResponse> getByCustId(@PathVariable String custId) {

		ApiResponse response;
		try {

			Object cust = adminService.findCustomerByCustId(custId);
			if(cust == null)
			{
				response = new ApiResponse("No such customer found.", HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
			}
			response = new ApiResponse("Fetch successful.", cust, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
			
		} catch(Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
