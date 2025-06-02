package com.example.adminServiceOfBank.payload.request;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewEmpRequest {

	private String empId;
	private String name;
	private String mobileNumber;
	private String email;
	private String password;
	private Date dob;
	private String address;
	private String aadhaarNumber;
	private int branchId;
	private List<Integer> roles;
	
}
