package com.example.adminServiceOfBank.payload.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCustomerRequest {

	private String custId;
	private String name;
	private String mobileNumber;
	private String email;
	private LocalDate dob;
	private String address;  
	private String aadhaarNumber;
	private String panNumber;
	private int branchId;
	private List<Integer> roles;
}
