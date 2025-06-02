package com.example.adminServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WelcomeEmailRequest {

	private String name, custId, password, address, branchName, 
		branchAddress, mobileNumber, accountNumber, email;
	
	private int branchCode;
}
