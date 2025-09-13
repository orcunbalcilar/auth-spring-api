package com.example.authapi.dto;

public record VerifyResponse(
    String status,
    UserInfo user,
    SessionInfo session
) {
    public static VerifyResponse of(UserInfo user, SessionInfo session) {
        return new VerifyResponse("ok", user, session);
    }
}
