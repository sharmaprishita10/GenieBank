package com.example.accountServiceOfBank.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.accountServiceOfBank.model.Account;
import com.example.accountServiceOfBank.payload.request.UpdateBalanceRequest;
import com.example.accountServiceOfBank.payload.response.ApiResponse;
import com.example.accountServiceOfBank.service.AccountService;


@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/add-account")
	public ResponseEntity<ApiResponse> addAccount(@RequestBody Account account) {

		String newAccountNumber = accountService.addAccount(account);
		ApiResponse response = new ApiResponse("Account added successfully!", newAccountNumber,
				HttpStatus.CREATED.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);

	}

	@PostMapping("/get-id-by-accNum")
	public ResponseEntity<ApiResponse> getIdByAccNum(@RequestBody String accNum) {

		ApiResponse response;

		Account account = accountService.findIdByAccNum(accNum);
		if (account == null) {
			response = new ApiResponse("No such account found.", HttpStatus.OK.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
		}
		response = new ApiResponse("Id Fetch successful.", account.getId(), HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@PutMapping("/update-balance")
	public ResponseEntity<ApiResponse> updateBalance(@RequestBody UpdateBalanceRequest updateBalRequest) {

		accountService.updateBalance(updateBalRequest);
		ApiResponse response = new ApiResponse("Account balance updated successfully.", HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@GetMapping("/get-balance/{id}")
	public ResponseEntity<ApiResponse> getBalance(@PathVariable int id) {

		double balance = accountService.getBalance(id);
		ApiResponse response = new ApiResponse("Account balance fetched successfully.", balance, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@GetMapping("/get-id-by-custId/{customerId}")
	public ResponseEntity<ApiResponse> getIdByCustId(@PathVariable int customerId) {

		int accountId = accountService.findIdByCustId(customerId);
		ApiResponse response = new ApiResponse("Id Fetch successful.", accountId, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}
}
