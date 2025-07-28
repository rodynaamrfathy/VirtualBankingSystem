package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {
    private UUID transactionId;
    private String status;
    private LocalDateTime timestamp;

}
