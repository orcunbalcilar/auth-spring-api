package com.example.authapi.dto;

public record SessionInfo(
    long timeRemaining,
    long expiresAt
) {}
