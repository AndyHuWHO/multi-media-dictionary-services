package com.word.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    private final GatewayFilter loginResponseFilter;
    @Value("${auth.url}")
    private String authServiceUrl;
    @Value("${word.url}")
    private String wordServiceUrl;
    @Value("${user.url}")
    private String userServiceUrl;
    @Value("${media.url}")
    private String mediaServiceUrl;

    public GatewayRoutesConfig(GatewayFilter loginResponseFilter) {
        this.loginResponseFilter = loginResponseFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-login", r -> r
                        .path("/api/auth/login", "/api/auth/get-member")
                        .filters(f -> f.filter(loginResponseFilter))
                        .uri(authServiceUrl))
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authServiceUrl))
                .route("word-service", r -> r
                        .path("/api/words/**")
                        .uri(wordServiceUrl))
                .route("user-service", r -> r
                        .path("/api/user/**")
                        .uri(userServiceUrl))
                .route("media-service", r -> r
                        .path("/api/media/**")
                        .uri(mediaServiceUrl))
                .route("word-docs", r -> r
                        .path("/word/v3/api-docs")
                        .filters(f -> f.rewritePath("/word/(.*)", "/$1"))
                        .uri(userServiceUrl))
                .route("user-docs", r -> r
                        .path("/user/v3/api-docs")
                        .filters(f -> f.rewritePath("/user/(.*)", "/$1"))
                        .uri(userServiceUrl))
                .build();
    }
}
