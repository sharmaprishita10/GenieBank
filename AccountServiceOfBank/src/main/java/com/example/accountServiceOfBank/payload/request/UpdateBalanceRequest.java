package com.example.accountServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBalanceRequest {

	private int id;
	private String updateType;
	private double amount;
}
