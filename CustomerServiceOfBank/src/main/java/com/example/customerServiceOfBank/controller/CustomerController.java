package com.example.customerServiceOfBank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.customerServiceOfBank.model.Customer;
import com.example.customerServiceOfBank.payload.request.TransferRequest;
import com.example.customerServiceOfBank.payload.response.ApiResponse;
import com.example.customerServiceOfBank.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService custService;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/add-customer")
	public ResponseEntity<ApiResponse> addCustomer(@RequestBody Customer customer) {
		ApiResponse response;

		String custId = custService.addCustomer(customer);
		response = new ApiResponse("Customer registered successfully!", custId, HttpStatus.CREATED.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);

	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/get-customer-by-custId/{custId}")
	public ResponseEntity<ApiResponse> getByCustId(@PathVariable String custId) {

		ApiResponse response;
		Customer cust = custService.findCustomerByCustId(custId);
		if (cust == null) {
			response = new ApiResponse("No such customer found.", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		response = new ApiResponse("Fetch successful.", cust, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/view-my-profile")
	public ResponseEntity<ApiResponse> viewProfile(@RequestHeader("X-User") String username) {

		ApiResponse response;

		Customer cust = custService.findCustomerByCustId(username);
		response = new ApiResponse("Fetch successful.", cust, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@GetMapping("/get-id-by-custId/{custId}")
	public ResponseEntity<ApiResponse> getIdByCustId(@PathVariable String custId) {

		ApiResponse response;

		Customer cust = custService.findCustomerByCustId(custId);
		if (cust == null) {
			response = new ApiResponse("No such customer found.", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		response = new ApiResponse("Id Fetch successful.", cust.getId(), HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PutMapping("/activate-netbanking")
	public ResponseEntity<ApiResponse> activateNetbanking(@RequestHeader("X-User") String username) {
		ApiResponse response;
		boolean activated = custService.activateNetbanking(username);
		if (activated) {
			response = new ApiResponse("Netbanking activated successfully!", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} else {
			response = new ApiResponse("Netbanking already active.", HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PostMapping("/transfer")
	public ResponseEntity<ApiResponse> transferAmount(@RequestHeader("X-User") String username,
			@RequestBody TransferRequest transferRequest) {

		ApiResponse response;
		String message = custService.transferAmount(username, transferRequest);
		if (message == null) {
			throw new NoSuchElementException();
		}

		response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/statement")
	public ResponseEntity<ApiResponse> getStatement(@RequestHeader("X-User") String username,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

		ApiResponse response;

		if (to.isBefore(from)) {

			response = new ApiResponse("End date cannot be before the start date.", HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Object data = custService.getStatement(username, from, to);
		List statements = (List) data;
		if (statements.isEmpty()) {
			response = new ApiResponse("No records found.", data, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		} else {
			response = new ApiResponse("Statement fetched successfully!", data, HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}

	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/get-balance")
	public ResponseEntity<ApiResponse> getBalance(@RequestHeader("X-User") String username) {

		double balance = custService.getBalance(username);
		ApiResponse response = new ApiResponse("Account balance fetched successfully.", "INR " + balance,
				HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}
}
