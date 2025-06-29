package com.word.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.security.SignatureException;
import java.util.Base64;

@Component
public class JwtUtil {
    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT");
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUserRole(String token) {
        return getClaims(token).get("role", String.class);
    }
//    @Value("${jwt.secret}")
//    private String secret;
//
//    private SecretKey secretKey;
//
//    @PostConstruct
//    public void init() {
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public Claims validateToken(String token) throws JwtException {
//        return Jwts
//                .parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseClaimsJws(token)
//
//    }
}
