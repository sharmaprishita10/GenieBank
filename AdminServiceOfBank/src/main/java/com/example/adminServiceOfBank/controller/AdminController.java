package com.example.adminServiceOfBank.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/register-employee")
	public ResponseEntity<ApiResponse> addEmployee(@RequestBody NewEmpRequest empDto) {

		adminService.addEmployee(empDto);
		ApiResponse response = new ApiResponse("Employee registered and corresponding user added successfully!",
				HttpStatus.CREATED.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/send-sms-otp")
	public ResponseEntity<ApiResponse> sendSmsOtp(@RequestBody MobileNumberRequest mobileNumberRequest) {

		adminService.sendSmsOtp(mobileNumberRequest.getMobileNumber());
		ApiResponse response = new ApiResponse("OTP sent successfully!", HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/verify-sms-otp")
	public ResponseEntity<ApiResponse> verifySmsOtp(@RequestBody MobileOtpRequest mobileOtpRequest) {

		String message = adminService.verifySmsOtp(mobileOtpRequest.getMobileNumber(), mobileOtpRequest.getMobileOtp());
		ApiResponse response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/send-email-otp")
	public ResponseEntity<ApiResponse> sendEmailOtp(@RequestBody EmailRequest emailRequest) {

		adminService.sendEmailOtp(emailRequest.getEmail());
		ApiResponse response = new ApiResponse("OTP sent successfully!", HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/verify-email-otp")
	public ResponseEntity<ApiResponse> verifyEmailOtp(@RequestBody EmailOtpRequest emailOtpRequest) {

		String message = adminService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getEmailOtp());
		ApiResponse response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/register-customer")
	public ResponseEntity<ApiResponse> addCustomer(@RequestHeader("X-User") String username,
			@RequestBody NewCustomerRequest custDto) {

		String message = adminService.addCustomer(username, custDto);
		ApiResponse response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/open-account")
	public ResponseEntity<ApiResponse> openAccount(@RequestHeader("X-User") String username,
			@RequestBody CustIdRequest custIdRequest) throws IOException {

		adminService.openAccount(username, custIdRequest.getCustId());
		ApiResponse response = new ApiResponse("Account opened successfully!", HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("deposit")
	public ResponseEntity<ApiResponse> depositAmount(@RequestHeader("X-User") String username,
			@RequestBody AccountNumRequest accNumRequest) {

		String data = adminService.depositAmount(username, accNumRequest.getAccountNumber(), accNumRequest.getAmount());
		if (data == null) {
			throw new NoSuchElementException();
		}
		ApiResponse response = new ApiResponse("Amount deposited successfully!", HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("withdraw")
	public ResponseEntity<ApiResponse> withdrawAmount(@RequestHeader("X-User") String username,
			@RequestBody AccountNumRequest accNumRequest) {

		String message = adminService.withdrawAmount(username, accNumRequest.getAccountNumber(),
				accNumRequest.getAmount());
		if (message == null) {
			throw new NoSuchElementException();
		}

		ApiResponse response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/{custId}/statement")
	public ResponseEntity<ApiResponse> getStatement(@PathVariable String custId,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		ApiResponse response;

		if (to.isBefore(from)) {

			response = new ApiResponse("End date cannot be before the start date.", HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Object data = adminService.getStatement(custId, from, to);
		if (data == null) {
			throw new NoSuchElementException();
		}

		List statements = (List) data;
		if (statements.isEmpty()) {
			response = new ApiResponse("No records found.", data, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} else {
			response = new ApiResponse("Statement fetched successfully!", data, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/get-balance/{custId}")
	public ResponseEntity<ApiResponse> getStatement(@PathVariable String custId) {

		Double balance = adminService.getBalance(custId);
		if (balance == null) {
			throw new NoSuchElementException();
		}

		ApiResponse response = new ApiResponse("Account balance fetched successfully.", "INR " + balance,
				HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/get-customer-by-custId/{custId}")
	public ResponseEntity<ApiResponse> getByCustId(@PathVariable String custId) {

		Object cust = adminService.findCustomerByCustId(custId);
		if (cust == null) {
			throw new NoSuchElementException();
		}
		ApiResponse response = new ApiResponse("Fetch successful.", cust, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}
}
