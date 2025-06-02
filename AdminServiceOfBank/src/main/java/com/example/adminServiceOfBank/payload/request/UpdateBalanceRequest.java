package com.example.adminServiceOfBank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBalanceRequest {

	private int id;
	private String updateType;
	private double amount;
}
