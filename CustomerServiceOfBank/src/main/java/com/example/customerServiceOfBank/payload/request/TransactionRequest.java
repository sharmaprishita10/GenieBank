package com.example.customerServiceOfBank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionRequest {

	private int fromAccount;
	private int toAccount;
	private double amount;
	private String transactionType;
    private String createdBy; 		// empId
    private String modeOfTransaction;
}
