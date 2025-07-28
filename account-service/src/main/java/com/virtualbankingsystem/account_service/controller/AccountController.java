package com.virtualbankingsystem.account_service.controller;

import com.virtualbankingsystem.account_service.dto.AccountResponse;
import com.virtualbankingsystem.account_service.dto.CreateAccountRequest;
import com.virtualbankingsystem.account_service.dto.TransferRequest;
import com.virtualbankingsystem.account_service.producer.RequestLoggerProducer;
import com.virtualbankingsystem.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final RequestLoggerProducer kafkaLogProducer;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        kafkaLogProducer.logRequest(request);
        try {
            AccountResponse response = accountService.createAccount(request);
            kafkaLogProducer.logResponse(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            );
            kafkaLogProducer.logResponse(error);
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        kafkaLogProducer.logRequest(request);
        try {
            accountService.transfer(request);
            Map<String, Object> response = Map.of("message", "Transfer successful.");
            kafkaLogProducer.logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Transfer Failed",
                    "message", e.getMessage()
            );
            kafkaLogProducer.logResponse(error);
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable UUID accountId) {
        kafkaLogProducer.logRequest(Map.of("accountId", accountId));
        return accountService.getAccount(accountId)
                .map(account -> {
                    AccountResponse response = AccountResponse.builder()
                            .accountId(account.getId())
                            .accountNumber(account.getAccountNumber())
                            .accountType(account.getAccountType().name())
                            .balance(account.getBalance())
                            .status(account.getStatus().name())
                            .build();
                    kafkaLogProducer.logResponse(response);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Account not found."
                    );
                    kafkaLogProducer.logResponse(error);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body((AccountResponse) error);
                });
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable UUID userId) {
        kafkaLogProducer.logRequest(Map.of("userId", userId));
        List<AccountResponse> responses = accountService.getAccountsByUser(userId);
        if (responses.isEmpty()) {
            Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No accounts found for user ID " + userId
            );
            kafkaLogProducer.logResponse(error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        kafkaLogProducer.logResponse(responses);
        return ResponseEntity.ok(responses);
    }
}
