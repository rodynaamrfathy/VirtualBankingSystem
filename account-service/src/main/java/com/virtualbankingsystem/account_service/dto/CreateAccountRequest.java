package com.virtualbankingsystem.account_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateAccountRequest {
    private UUID userId;
    private String accountType;
    private BigDecimal initialBalance;
}

