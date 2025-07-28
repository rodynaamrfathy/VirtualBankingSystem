package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionHistoryResponse {
    private UUID accountId;
    private BigDecimal amount;
    private String description;

}
