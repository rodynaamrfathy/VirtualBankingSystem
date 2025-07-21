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
    private String email;
    private String firstName;
    private String lastName;

    // Constructors
    public UserResponse() {}

    public UserResponse(UUID userId, String username, String email, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
