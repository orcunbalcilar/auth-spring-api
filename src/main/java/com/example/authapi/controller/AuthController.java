package com.example.authapi.controller;

import com.example.authapi.dto.*;
import com.example.authapi.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    @Value("${session.lifetime}")
    private Long sessionLifetime;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, 
            HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);
            
            // Generate access token and refresh token
            String accessToken = authService.generateAccessToken(request.email());
            String refreshToken = authService.generateRefreshToken(request.email());
            
            // Set access token cookie
            Cookie accessTokenCookie = new Cookie("access_token", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set to true in production
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(sessionLifetime.intValue());
            
            // Set refresh token cookie
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set to true in production
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(sessionLifetime.intValue() * 7); // 7 times longer than access token
            
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Email and password are required")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new LoginResponse(e.getMessage(), null)
                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponse("Invalid credentials", null)
                );
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new LoginResponse("Internal server error", null)
            );
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse response) {
        try {
            LogoutResponse logoutResponse = authService.logout();
            
            // Clear access token cookie
            Cookie accessTokenCookie = new Cookie("access_token", "");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set to true in production
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(0);
            
            // Clear refresh token cookie
            Cookie refreshTokenCookie = new Cookie("refresh_token", "");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Set to true in production
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            
            return ResponseEntity.ok(logoutResponse);
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new LogoutResponse("Internal server error")
            );
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verify(HttpServletRequest request) {
        try {
            String accessToken = getAccessTokenFromCookies(request);
            
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Access token not found")
                );
            }
            
            VerifyResponse verifyResponse = authService.verify(accessToken);
            return ResponseEntity.ok(verifyResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(e.getMessage())
            );
        } catch (Exception e) {
            System.err.println("Token verification error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("Invalid access token")
            );
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request, HttpServletResponse response) {
        try {
            TokenResponse tokenResponse = authService.refreshToken(request);
            
            // Set new access token cookie
            Cookie accessTokenCookie = new Cookie("access_token", tokenResponse.accessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set to true in production
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(sessionLifetime.intValue());
            
            response.addCookie(accessTokenCookie);
            
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(e.getMessage())
            );
        } catch (Exception e) {
            System.err.println("Token refresh error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("Failed to refresh token")
            );
        }
    }
    
    private String getAccessTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
