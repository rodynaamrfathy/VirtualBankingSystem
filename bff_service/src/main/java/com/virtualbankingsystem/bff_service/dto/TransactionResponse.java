package com.virtualbankingsystem.bff_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private String toAccountId;
    private String description;
    private String timestamp;
}