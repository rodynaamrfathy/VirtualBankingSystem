package com.virtualbankingsystem.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserResponse {
    // Getters and Setters
    private UUID userId;
    private String username;
    

    // Constructors
    public UserResponse() {}

    public UserResponse(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
        
    }

}
