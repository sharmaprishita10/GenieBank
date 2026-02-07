package com.example.transactionServiceOfBank.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.transactionServiceOfBank.controller.TransactionController;
import com.example.transactionServiceOfBank.payload.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
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
