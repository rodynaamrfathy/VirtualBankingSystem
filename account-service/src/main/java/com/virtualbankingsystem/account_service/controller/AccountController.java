package com.virtualbankingsystem.account_service.controller;

import com.virtualbankingsystem.account_service.model.Account;
import com.virtualbankingsystem.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Map<String, Object> body) {
        try {
            UUID userId = UUID.fromString((String) body.get("userId"));
            String type = (String) body.get("accountType");
            BigDecimal initialBalance = new BigDecimal(body.get("initialBalance").toString());
            Account.AccountType accountType = Account.AccountType.valueOf(type);
            Account account = accountService.createAccount(userId, accountType, initialBalance);
            Map<String, Object> resp = new HashMap<>();
            resp.put("accountId", account.getId());
            resp.put("accountNumber", account.getAccountNumber());
            resp.put("message", "Account created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable UUID accountId) {
        return accountService.getAccount(accountId)
            .map(account -> ResponseEntity.ok(Map.of(
                "accountId", account.getId(),
                "accountNumber", account.getAccountNumber(),
                "accountType", account.getAccountType(),
                "balance", account.getBalance(),
                "status", account.getStatus()
            )))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", "Account with ID " + accountId + " not found."
            )));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable UUID userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", "No accounts found for user ID " + userId + "."
            ));
        }
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Account account : accounts) {
            resp.add(Map.of(
                "accountId", account.getId(),
                "accountNumber", account.getAccountNumber(),
                "accountType", account.getAccountType(),
                "balance", account.getBalance(),
                "status", account.getStatus()
            ));
        }
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> body) {
        try {
            UUID fromAccountId = UUID.fromString((String) body.get("fromAccountId"));
            UUID toAccountId = UUID.fromString((String) body.get("toAccountId"));
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            accountService.transfer(fromAccountId, toAccountId, amount);
            return ResponseEntity.ok(Map.of("message", "Account updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
            ));
        }
    }
} 