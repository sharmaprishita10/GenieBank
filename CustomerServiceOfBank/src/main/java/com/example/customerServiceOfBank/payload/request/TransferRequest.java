package com.example.customerServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {

	private String accountNumber;
	private double amount;
	private String transferType;
}
