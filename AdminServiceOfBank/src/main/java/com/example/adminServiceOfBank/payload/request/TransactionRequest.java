package com.example.adminServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {

	private int fromAccount;
	private int toAccount;
	private double amount;
	private String transactionType;
    private String createdBy; 		// empId
    private String modeOfTransaction;
}
