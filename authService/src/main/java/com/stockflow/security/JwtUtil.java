package com.stockflow.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String secret = "mySecretKeyForJWTTokenSigningAndVerificationProcess2025!";
    private long expiration = 1000 * 60 * 60; // 1 hr

    public String generateToken(String username, String role, Long companyId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("companyId", companyId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public Long extractCompanyId(String token) {
        Object val = getClaims(token).get("companyId");
        if (val == null) return null;
        return ((Number) val).longValue();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
