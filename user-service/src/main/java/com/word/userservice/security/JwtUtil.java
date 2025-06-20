package com.word.userservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
//        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
//        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        byte[] keyBytes = Base64.getDecoder()
                .decode(jwtSecret.trim());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("jwtSecret: " + jwtSecret);
//        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public String generateToken(String publicId, String email, String role) {
        long expirationMs = 86400000;
        return Jwts.builder()
                .subject(publicId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
