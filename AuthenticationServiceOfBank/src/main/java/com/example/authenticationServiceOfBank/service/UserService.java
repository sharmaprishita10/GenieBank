package com.example.authenticationServiceOfBank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authenticationServiceOfBank.model.Role;
import com.example.authenticationServiceOfBank.model.User;
import com.example.authenticationServiceOfBank.payload.request.*;
import com.example.authenticationServiceOfBank.repository.RoleRepository;
import com.example.authenticationServiceOfBank.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}

	// To insert the new token/null in user table
	public void insertToken(String username, String token) {
		User user = userRepo.findByUsername(username);
		user.setToken(token);
		userRepo.save(user);
	}

	// Verify given token with the one stored in the db
	public boolean checkToken(String username, String token) {
		User user = userRepo.findByUsername(username);
		String authToken = user.getToken();

		if (token.equals(authToken)) {
			return true;
		} else {
			return false;
		}
	}

	public void addUser(NewUserRequest userDto) {

		User user = mapper.map(userDto, User.class);
		List<Role> roles = roleRepo.findAllById(userDto.getRoles());
		user.setRoles(roles);
		userRepo.save(user);
	}

	public String changePassword(String username, ChangePasswordRequest changePasswordRequest) {
		
		String password = changePasswordRequest.getNewPassword();
		String message;
		if(password.equals(changePasswordRequest.getConfirmPassword()))
		{
			User user = userRepo.findByUsername(username);
			user.setPassword(encoder.encode(password));
			userRepo.save(user);
			message = "Password changed successfully!";
		}
		else
		{
			message = "Passwords mismatch. Password reset failed.";
		}
		
		return message;
	}

}
