package com.virtualbankingsystem.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UserLoginResponse {
    // Getters and Setters
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    // Constructors
    public UserLoginResponse() {}

    public UserLoginResponse(UUID userId, String username, String email, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
