package com.example.authapi.dto;

public record LoginResponse(
    String message,
    UserInfo user
) {}
