package com.example.adminServiceOfBank.exceptionHandler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.adminServiceOfBank.controller.AdminController;
import com.example.adminServiceOfBank.payload.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(DataIntegrityViolationException e) {
		
		logger.error("Data integrity violation: {}", e.getMostSpecificCause().getMessage(), e);
		String errorMessage = e.getMostSpecificCause().getMessage(); 
		int index = errorMessage.indexOf(" for");
		String result = (index != -1) ? errorMessage.substring(0, index) : errorMessage;
		
		ApiResponse response = new ApiResponse(result, HttpStatus.CONFLICT.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleValidation(ConstraintViolationException e) {
        
		String errorMessage = e.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)   
			    .collect(Collectors.joining("; "));

		logger.error("Constraint violations: {}", errorMessage, e);
		ApiResponse response = new ApiResponse(errorMessage, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NoSuchElementException e) {
    	
    	logger.error("No Such Element Exception: {}", e);
        ApiResponse response = new ApiResponse("Resource not found", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(com.twilio.exception.ApiException.class)
    public ResponseEntity<ApiResponse> handleTwilioApiException(com.twilio.exception.ApiException e) {
    	
        logger.error("Twilio API error: {}", e.getMessage(), e);
        ApiResponse response = new ApiResponse("OTP service error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationDenied(AuthorizationDeniedException e) {
    	
        logger.error("Access Denied", e);
        ApiResponse response = new ApiResponse("Access Denied: You do not have permission to access this resource.", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<ApiResponse>(response, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAll(Exception e) {
    	
        logger.error("Unhandled exception:", e);
        ApiResponse response = new ApiResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
