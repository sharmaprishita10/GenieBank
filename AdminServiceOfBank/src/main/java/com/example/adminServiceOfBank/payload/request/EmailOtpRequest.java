package com.example.adminServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailOtpRequest {

	private String email;
	private String emailOtp;
}
