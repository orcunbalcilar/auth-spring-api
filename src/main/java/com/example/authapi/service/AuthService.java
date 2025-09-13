package com.example.authapi.service;

import com.example.authapi.dto.*;
import com.example.authapi.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    
    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    public LoginResponse login(LoginRequest request) {
        // Validate input
        if (request.email() == null || request.email().isBlank() || 
            request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        // Authenticate user with plain text password comparison (for mock data)
        if (!userService.authenticateUser(request.email(), request.password())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Find user
        User user = userService.findByEmail(request.email());
        
        // Generate access token with session start time and constant lifetime
        String accessToken = jwtService.generateToken(user.getId().toString(), user.getEmail());
        
        return new LoginResponse(
            "Login successful",
            UserInfo.from(user)
        );
    }
    
    public LogoutResponse logout() {
        return new LogoutResponse("Logout successful");
    }
    
    public String generateAccessToken(String email) {
        User user = userService.findByEmail(email);
        return jwtService.generateToken(user.getId().toString(), user.getEmail());
    }
    
    public VerifyResponse verify(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token not found");
        }
        
        // Check if session lifetime has ended
        if (jwtService.isSessionExpired(accessToken)) {
            throw new IllegalArgumentException("Session expired");
        }
        
        // Verify token
        if (!jwtService.isTokenValid(accessToken)) {
            throw new IllegalArgumentException("Invalid access token");
        }
        
        String email = jwtService.extractEmail(accessToken);
        User user = userService.findByEmail(email);
        
        // Calculate remaining time for response
        long timeRemaining = jwtService.getTimeRemaining(accessToken);
        long expiresAt = jwtService.getSessionExpiry(accessToken);
        
        SessionInfo sessionInfo = new SessionInfo(timeRemaining, expiresAt);
        
        return VerifyResponse.of(UserInfo.from(user), sessionInfo);
    }
}
