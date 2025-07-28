package com.virtualbankingsystem.transaction_service.controller;

import com.virtualbankingsystem.transaction_service.dto.*;
import com.virtualbankingsystem.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody TransferInitiationRequest request) {
        return transactionService.initiateTransfer(request);
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody TransferExecutionRequest request) {
        return transactionService.executeTransfer(request);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable UUID accountId) {
        return transactionService.getTransactionsForAccount(accountId);
    }
}
