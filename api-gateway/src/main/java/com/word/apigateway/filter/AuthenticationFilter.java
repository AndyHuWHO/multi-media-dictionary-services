package com.word.apigateway.filter;

import com.word.apigateway.config.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter  implements GlobalFilter, Ordered {
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip auth-service and word-service
        if (path.startsWith("/api/auth/") ||
                path.startsWith("/api/words/") ||
                path.startsWith("/api/media/word/") ||
                path.startsWith("/api/media/feed") ||
                path.startsWith("/api/user/profile/visit") ||
                path.startsWith("/words/v3/api-docs") ||
                path.startsWith("/user/v3/api-docs") ||
                path.startsWith("/media/v3/api-docs")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            jwtUtil.validateToken(token);
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);

            // Enforce MEMBER role for all /api/user/notebooks routes
            if (path.startsWith("/api/user/notebooks") && !"MEMBER".equals(role)) {
                return forbidden(exchange, "Requires MEMBER role");
            }

            // Add X-Auth-UserId to the request
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-Auth-UserId", userId)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            return chain.filter(mutatedExchange);

        } catch (JwtException ex) {
            return unauthorized(exchange, ex.getMessage());
        }
    }


    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        System.out.println("auth error: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        System.out.println("auth error: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
