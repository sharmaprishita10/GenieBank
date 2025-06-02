package com.example.apiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.example.apiGateway.filter.JwtAuthFilter;

@Configuration
public class WebSecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public WebSecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		
		http.csrf(csrf -> csrf.disable()).httpBasic(basic -> basic.disable()).formLogin(form -> form.disable())
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
				.authorizeExchange(
						exchanges -> exchanges.pathMatchers("/auth/sign-in", "/auth/validate").permitAll()
						.anyExchange().permitAll());
		
		return http.build();
	}
}
