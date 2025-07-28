package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {

    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;

    public TransferRequest() {
    }

    public TransferRequest(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String currency, String description) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }
}
