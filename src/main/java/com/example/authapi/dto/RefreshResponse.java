package com.example.authapi.dto;

public record RefreshResponse(
    String message,
    String accessToken
) {}
