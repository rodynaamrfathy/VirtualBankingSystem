package com.virtualbankingsystem.user_service.exception;

import lombok.Getter;

@Getter
public class CustomErrorResponse {
    // Getters and setters
    private int status;
    private String error;
    private String message;

    public CustomErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
