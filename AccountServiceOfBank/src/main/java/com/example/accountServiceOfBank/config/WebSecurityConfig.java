package com.example.accountServiceOfBank.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.accountServiceOfBank.filter.GatewayHeaderFilter;
import com.example.accountServiceOfBank.payload.response.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

	@Autowired
	private GatewayHeaderFilter gatewayHeaderFilter;

	@Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http   
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
	      .csrf(csrf -> csrf.disable())
		.exceptionHandling(exceptionHandling ->
        exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
  
        		ApiResponse apiResponse = new ApiResponse("Access Denied: You do not have permission to access this resource.", HttpServletResponse.SC_FORBIDDEN);
        		
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); 
                response.setContentType("application/json");
                response.getWriter().write(apiResponse.toString());
            })                        
        )
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests
						.requestMatchers(
					              "/v3/api-docs/**",
					              "/swagger-ui.html",
					              "/swagger-ui/**",
					              "/swagger-ui/index.html",
					              "/swagger-resources/**",
					              "/webjars/**"
					          ).permitAll()
						.requestMatchers("/account/**").permitAll())
				.addFilterBefore(gatewayHeaderFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // apply to all paths
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
