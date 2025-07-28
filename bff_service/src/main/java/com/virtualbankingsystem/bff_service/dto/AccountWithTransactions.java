package com.virtualbankingsystem.bff_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AccountWithTransactions {
    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private List<TransactionDto> transactions;
}