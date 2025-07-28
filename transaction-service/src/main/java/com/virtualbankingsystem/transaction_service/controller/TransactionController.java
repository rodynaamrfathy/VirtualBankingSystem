package com.virtualbankingsystem.transaction_service.controller;

import com.virtualbankingsystem.transaction_service.client.AccountClient;
import com.virtualbankingsystem.transaction_service.dto.*;
import com.virtualbankingsystem.transaction_service.producer.RequestLoggerProducer;
import com.virtualbankingsystem.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountClient accountClient;
    private final RequestLoggerProducer logger;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody TransferInitiationRequest request) {
        logger.logRequest(request);

        try {
            TransferInitiationResponse response = transactionService.initiateTransfer(request);
            logger.logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            );
            logger.logResponse(error);
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody TransferExecutionRequest request) {
        logger.logRequest(request);

        try {
            TransactionResponse response = transactionService.processTransfer(request.getTransactionId());
            logger.logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Transfer Execution Failed",
                    "message", e.getMessage()
            );
            logger.logResponse(error);
            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable UUID accountId) {
        logger.logRequest(Map.of("accountId", accountId));

        List<TransactionHistoryResponse> transactions = transactionService.getTransactionsForAccount(accountId);
        if (transactions.isEmpty()) {
            Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No transactions found for account ID " + accountId
            );
            logger.logResponse(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        logger.logResponse(transactions);
        return ResponseEntity.ok(transactions);
    }
}
