package com.example.authapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${session.lifetime}")
    private Long sessionLifetime;
    
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }
    
    public Long extractSessionStart(String token) {
        return extractClaim(token, claims -> claims.get("sessionStart", Long.class));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(String userId, String email) {
        return generateToken(userId, email, sessionLifetime);
    }
    
    public String generateRefreshToken(String userId, String email) {
        // Refresh tokens last longer (7 days in seconds)
        return generateToken(userId, email, sessionLifetime * 7);
    }
    
    private String generateToken(String userId, String email, Long lifetime) {
        Map<String, Object> claims = new HashMap<>();
        long sessionStart = System.currentTimeMillis() / 1000; // Unix timestamp in seconds
        long sessionExpiry = sessionStart + lifetime;
        
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("sessionStart", sessionStart);
        
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(sessionExpiry * 1000)) // Convert back to milliseconds for Date
                .signWith(getSignInKey())
                .compact();
    }
    
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            long currentTime = System.currentTimeMillis() / 1000;
            long sessionStart = claims.get("sessionStart", Long.class);
            long sessionExpiry = sessionStart + sessionLifetime;
            
            return currentTime < sessionExpiry && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isSessionExpired(String token) {
        try {
            long currentTime = System.currentTimeMillis() / 1000;
            long sessionStart = extractSessionStart(token);
            long sessionExpiry = sessionStart + sessionLifetime;
            
            return currentTime >= sessionExpiry;
        } catch (Exception e) {
            return true;
        }
    }
    
    public long getTimeRemaining(String token) {
        try {
            long currentTime = System.currentTimeMillis() / 1000;
            long sessionStart = extractSessionStart(token);
            long sessionExpiry = sessionStart + sessionLifetime;
            
            return Math.max(0, sessionExpiry - currentTime);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public long getSessionExpiry(String token) {
        try {
            long sessionStart = extractSessionStart(token);
            return sessionStart + sessionLifetime;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public Long getSessionLifetime() {
        return sessionLifetime;
    }
}
