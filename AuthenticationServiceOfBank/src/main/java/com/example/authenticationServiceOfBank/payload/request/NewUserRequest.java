package com.example.authenticationServiceOfBank.payload.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserRequest {
	
	private String username;
	private String password;
	private List<Integer> roles;
}
