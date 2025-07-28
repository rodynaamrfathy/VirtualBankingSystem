package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountTransactionRequest {
    private UUID accountId;
    private BigDecimal amount;
    private String description;

}
