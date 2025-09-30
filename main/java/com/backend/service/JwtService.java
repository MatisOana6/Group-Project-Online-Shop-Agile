package com.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKeyRaw;

    private static String secretKey;

    @PostConstruct
    public void init() {
        JwtService.secretKey = secretKeyRaw;
    }

    public static String getSecretKey() {
        return secretKey;
    }

    @Value("${jwt.expirationMs}")
    private long expirationMillis;

    public String generateJwtToken(String username, String userId, String role) {
        Key key = getSigningKey(secretKey);
        return Jwts.builder()
                .setSubject(username)
                .setId(userId)
                .claim("role", List.of(role))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token is expired!");
            return false;
        } catch (SignatureException e) {
            System.out.println("Signature does not match the secret key");
            return false;
        }
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public UUID extractId(String token) {
        Key key = getSigningKey(secretKey);
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return UUID.fromString(claims.getId());
    }

    public static String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization") == null
                ? null
                : request.getHeader("Authorization").split("Bearer ")[1];
    }

    public static Key getSigningKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
