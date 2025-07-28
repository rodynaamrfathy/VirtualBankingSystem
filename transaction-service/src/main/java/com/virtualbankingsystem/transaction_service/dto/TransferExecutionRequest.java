package com.virtualbankingsystem.transaction_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TransferExecutionRequest {
    private UUID transactionId;

}