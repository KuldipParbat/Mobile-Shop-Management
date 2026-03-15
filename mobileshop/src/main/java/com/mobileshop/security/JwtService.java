package com.mobileshop.security;

import com.mobileshop.entity.Admin;
import com.mobileshop.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── User token ──
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("tenantId", user.getTenantId())
                .claim("role",     user.getRole().name())
                .claim("name",     user.getName())
                .claim("type",     "USER")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Admin token ──
    public String generateAdminToken(Admin admin) {
        return Jwts.builder()
                .setSubject(admin.getEmail())
                .claim("role", "ADMIN")
                .claim("name", admin.getName())
                .claim("type", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractTenantId(String token) {
        return extractClaims(token).get("tenantId", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}