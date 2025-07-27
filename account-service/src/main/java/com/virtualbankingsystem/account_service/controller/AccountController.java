package com.virtualbankingsystem.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualbankingsystem.account_service.KafkaLogProducer;
import com.virtualbankingsystem.account_service.dto.AccountResponse;
import com.virtualbankingsystem.account_service.dto.CreateAccountRequest;
import com.virtualbankingsystem.account_service.dto.TransferRequest;
import com.virtualbankingsystem.account_service.model.Account;
import com.virtualbankingsystem.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final KafkaLogProducer kafkaLogProducer;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        kafkaLogProducer.sendLog(String.valueOf(request), "Request");
        try {
            AccountResponse response = accountService.createAccount(request);
            kafkaLogProducer.sendLog(String.valueOf(response), "Response");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            );
            kafkaLogProducer.sendLog(error.toString(), "Response");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        kafkaLogProducer.sendLog(String.valueOf(request), "Request");
        try {
            accountService.transfer(request);
            Map<String, Object> response = Map.of("message", "Transfer successful.");
            kafkaLogProducer.sendLog(response.toString(), "Response");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Transfer Failed",
                    "message", e.getMessage()
            );
            kafkaLogProducer.sendLog(error.toString(), "Response");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable UUID accountId) {
        kafkaLogProducer.sendLog(Map.of("accountId", accountId).toString(), "Request");
        return accountService.getAccount(accountId)
                .map(account -> ResponseEntity.ok(AccountResponse.builder()
                        .accountId(account.getId())
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType().name())
                        .balance(account.getBalance())
                        .status(account.getStatus().name())
                        .build()))
                .orElseGet(() -> {
                    Map<String, Object> error = Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Account not found."
                    );
                    kafkaLogProducer.sendLog(error.toString(), "Response");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body((AccountResponse) error);
                });
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable UUID userId) {
        kafkaLogProducer.sendLog(Map.of("userId", userId).toString(), "Request");
        List<AccountResponse> responses = accountService.getAccountsByUser(userId);
        if (responses.isEmpty()) {
            Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No accounts found for user ID " + userId
            );
            kafkaLogProducer.sendLog(error.toString(), "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        kafkaLogProducer.sendLog(responses.toString(), "Response");
        return ResponseEntity.ok(responses);
    }
}
