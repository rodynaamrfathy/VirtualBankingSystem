package com.virtualbankingsystem.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    // Getters and Setters
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
