package com.virtualbankingsystem.transaction_service.controller;

import com.virtualbankingsystem.transaction_service.dto.*;
import com.virtualbankingsystem.transaction_service.producer.RequestLoggerProducer;
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
    private final RequestLoggerProducer requestLoggerProducer;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody TransferInitiationRequest request) {
        requestLoggerProducer.logRequest(request);
        ResponseEntity<?> response = transactionService.initiateTransfer(request);
        requestLoggerProducer.logResponse(response.getBody());
        return response;
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody TransferExecutionRequest request) {
        requestLoggerProducer.logRequest(request);
        ResponseEntity<?> response = transactionService.executeTransfer(request);
        requestLoggerProducer.logResponse(response.getBody());
        return response;
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable UUID accountId) {
        requestLoggerProducer.logRequest("Fetching transactions for accountId: " + accountId);
        ResponseEntity<?> response = transactionService.getTransactionsForAccount(accountId);
        requestLoggerProducer.logResponse(response.getBody());
        return response;
    }
}
