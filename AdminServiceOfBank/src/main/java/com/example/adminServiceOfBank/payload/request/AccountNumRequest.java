package com.example.adminServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountNumRequest {

	private String accountNumber;
	private double amount;
}
