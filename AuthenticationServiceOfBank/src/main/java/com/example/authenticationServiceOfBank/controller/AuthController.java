package com.example.authenticationServiceOfBank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationServiceOfBank.model.User;
import com.example.authenticationServiceOfBank.payload.request.*;
import com.example.authenticationServiceOfBank.payload.response.*;
import com.example.authenticationServiceOfBank.security.JwtUtil;
import com.example.authenticationServiceOfBank.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtils;

	@PostMapping("/sign-in")
	public ResponseEntity<ApiResponse> authenticateUser(@RequestBody User user) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String username = userDetails.getUsername();
		String token = jwtUtils.generateToken(userDetails);

		userService.insertToken(username, token); // Sign in

		ApiResponse response = new ApiResponse("Authentication successful", token, HttpStatus.OK.value());
		return ResponseEntity.ok(response);

	}

	@PostMapping("/sign-out")
	public ResponseEntity<ApiResponse> logoutUser(@RequestHeader("X-User") String username) {

		userService.insertToken(username, null); // Sign out

		ApiResponse response = new ApiResponse("You have been logged out.", HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/validate")
	public ResponseEntity<Boolean> validateToken(@RequestParam("user") String username,
			@RequestParam("token") String token) {
		return ResponseEntity.ok(userService.checkToken(username, token));
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
	@PostMapping("/add-user")
	public ResponseEntity<ApiResponse> addUser(@RequestBody NewUserRequest userDto) {

		userService.addUser(userDto);
		ApiResponse response = new ApiResponse("New user added successfully!", HttpStatus.CREATED.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);

	}

	@PutMapping("/change-password")
	public ResponseEntity<ApiResponse> changePassword(@RequestHeader("X-User") String username,
			@RequestBody ChangePasswordRequest changePasswordRequest) {

		String message = userService.changePassword(username, changePasswordRequest);
		ApiResponse response = new ApiResponse(message, HttpStatus.OK.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);

	}
}
