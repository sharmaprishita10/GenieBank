package com.example.adminServiceOfBank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewAccountRequest {

	private String accountNumber;
	private int customerId;
	private int createdBy;
}
