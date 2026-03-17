package com.stockflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final String SECRET = "mySecretKeyForJWTTokenSigningAndVerificationProcess2025!";

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public static Long extractCompanyId(String token) {
        Object val = getClaims(token).get("companyId");
        if (val == null) return null;
        return ((Number) val).longValue();
    }
}
