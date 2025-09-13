package com.example.authapi.dto;

import com.example.authapi.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserInfo(
    Long id,
    String username,
    String email,
    
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    
    String role
) {
    public static UserInfo from(User user) {
        return new UserInfo(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getRole().name()
        );
    }
}
