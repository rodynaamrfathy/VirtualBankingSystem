package com.virtualbankingsystem.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RegisterResponse {
    private UUID userId;
    private String username;
    private String message;

    // Constructors
    public RegisterResponse() {}

    public RegisterResponse(UUID userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }
} 