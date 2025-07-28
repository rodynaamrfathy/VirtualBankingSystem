package com.virtualbankingsystem.bff_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private UUID toAccountId;
    private String description;
    private LocalDateTime timestamp;
}
