package com.word.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    private final GatewayFilter loginResponseFilter;

    public GatewayRoutesConfig(GatewayFilter loginResponseFilter) {
        this.loginResponseFilter = loginResponseFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-login", r -> r
                        .path("/api/auth/login", "/api/auth/get-member")
                        .filters(f -> f.filter(loginResponseFilter))
                        .uri("http://localhost:8081"))
                .build();
    }
}
