package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferInitiationRequest {
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String description;

}
