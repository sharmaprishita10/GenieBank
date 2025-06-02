package com.example.authenticationServiceOfBank.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authenticationServiceOfBank.payload.response.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GatewayHeaderFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		String path = request.getServletPath();
		
		if (path.startsWith("/v3/api-docs") ||
		        path.startsWith("/swagger-ui") ||
		        path.equals("/swagger-ui.html")) {
			
		        chain.doFilter(request, response);
		        return;
		    }
		
		String userHeader = request.getHeader("X-User");
		String rolesHeader = request.getHeader("X-Roles");
	    
		
		// Public
		if ("/auth/sign-in".equals(path) || "/auth/validate".equals(path)) {
			
			// allow sign-in through
			chain.doFilter(request, response);
			return;
		}
		
		// Secured
		if (userHeader == null || userHeader.isBlank() || rolesHeader == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType("application/json");
	        
	        String message = "Access Denied.";
	        ApiResponse responseBody = new ApiResponse(message, HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(responseBody.toString());

			return;
		}
		
		List<GrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
		        .map(SimpleGrantedAuthority::new)
		        .collect(Collectors.toList());
		
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userHeader, null, authorities);
	    SecurityContextHolder.getContext().setAuthentication(auth);

		chain.doFilter(request, response);
	}
}
