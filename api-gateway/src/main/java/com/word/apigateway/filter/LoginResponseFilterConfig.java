package com.word.apigateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Configuration
public class LoginResponseFilterConfig {
    private final ObjectMapper objectMapper;
    private final ModifyResponseBodyGatewayFilterFactory responseFilterFactory;

    public LoginResponseFilterConfig(ObjectMapper objectMapper, ModifyResponseBodyGatewayFilterFactory responseFilterFactory) {
        this.objectMapper = objectMapper;
        this.responseFilterFactory = responseFilterFactory;
    }

    @Bean
    public GatewayFilter loginResponseFilter() {
        return responseFilterFactory.apply(config -> config
                .setRewriteFunction(String.class, String.class, (exchange, body) -> {
                    try {
                        String acceptHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ACCEPT);
                        boolean isBrowserClient = acceptHeader != null && acceptHeader.contains("text/html");
                        System.out.println("accept header: " + acceptHeader
                                + " is browser client: " + isBrowserClient + " body: " + body + "");

                        if (!isBrowserClient) {
                            return Mono.just(body);
                        }

                        JsonNode json = objectMapper.readTree(body);
                        if (!json.has("token")) return Mono.just(body);

                        String token = json.get("token").asText();
                        ((ObjectNode) json).remove("token");

                        ServerHttpResponse response = exchange.getResponse();
                        ResponseCookie cookie = ResponseCookie.from("token", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .sameSite("Lax")
                                .maxAge(3600)
                                .build();
                        response.addCookie(cookie);

                        return Mono.just(json.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.just(body);
                    }
                }));
    }

}
