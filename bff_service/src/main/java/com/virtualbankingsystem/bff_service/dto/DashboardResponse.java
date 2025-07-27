package com.virtualbankingsystem.bff_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class DashboardResponse {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<AccountResponse> accounts;
}
