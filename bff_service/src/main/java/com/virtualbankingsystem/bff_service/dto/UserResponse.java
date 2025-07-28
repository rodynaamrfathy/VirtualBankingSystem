package com.virtualbankingsystem.bff_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
