package com.example.adminServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileOtpRequest {
	
	private String mobileNumber;
	private String mobileOtp;
}
