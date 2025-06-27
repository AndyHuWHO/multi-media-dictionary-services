package com.word.authservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@Getter
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;
    @PostConstruct
    public void init() {
        System.out.println("Secret:" + secret);
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}
