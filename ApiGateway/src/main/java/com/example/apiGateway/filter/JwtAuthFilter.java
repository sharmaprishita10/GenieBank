package com.example.apiGateway.filter;

import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.apiGateway.client.AuthFeignClient;
import com.example.apiGateway.response.ApiResponse;
import com.example.apiGateway.util.JwtUtil;


@Component
public class JwtAuthFilter
    extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;
    private final AuthFeignClient authClient;

    public JwtAuthFilter(JwtUtil jwtUtil, @Lazy AuthFeignClient authClient) {
      super(Config.class);
      this.jwtUtil = jwtUtil;
      this.authClient = authClient;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest()
                                       .getHeaders()
                                       .getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return handleUnauthorized(exchange);
            }

            String token = authHeader.substring(7);
            String username;
            List<String> roles;
            
            try {
                // parse signature & expiry
                username = jwtUtil.getUsername(token);
                   roles = jwtUtil.getRoles(token);
                
            } catch (JwtException e) {
            	return handleUnauthorized(exchange);
            }

            // delegate active-check to Auth service
            boolean active = authClient.validateToken(username, token);
            if (!active) {
            	return handleUnauthorized(exchange);
            }

            // forward with username and roles as header
            String rolesList = String.join(",", roles);

            var mutated = exchange.mutate()
                .request(r -> r
                    .header("X-User",  username)
                    .header("X-Roles", rolesList)
                )
                .build();
            
            return chain.filter(mutated);
        };
    }

    public Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ApiResponse apiResponse = new ApiResponse("Access Denied.", HttpStatus.UNAUTHORIZED.value());
        byte[] bytes = apiResponse.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
    
    public static class Config {
    }
}
