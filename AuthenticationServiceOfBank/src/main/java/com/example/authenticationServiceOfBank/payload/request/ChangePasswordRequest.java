package com.example.authenticationServiceOfBank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

	private String newPassword;
	private String confirmPassword;
}
