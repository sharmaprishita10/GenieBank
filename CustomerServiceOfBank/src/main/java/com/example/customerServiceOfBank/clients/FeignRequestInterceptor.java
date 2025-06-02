package com.example.customerServiceOfBank.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignRequestInterceptor implements RequestInterceptor{

	@Autowired
    private HttpServletRequest request;

    @Override
    public void apply(RequestTemplate template) {
        
        String xUser = request.getHeader("X-User");
        if (xUser != null) {
            template.header("X-User", xUser);
        }
        
        String xRoles = request.getHeader("X-Roles");
        if (xRoles != null) {
            template.header("X-Roles", xRoles);
        }
    }
}
