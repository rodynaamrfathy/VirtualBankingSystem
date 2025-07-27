package com.virtualbankingsystem.account_service;

import com.virtualbankingsystem.account_service.KafkaLogProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    @Autowired
    private KafkaLogProducer kafkaLogProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Map<String, Object> body) {
        kafkaLogProducer.sendLog(toJson(body), "Request");
        try {
            UUID userId = UUID.fromString((String) body.get("userId"));
            String type = (String) body.get("accountType");
            BigDecimal initialBalance = new BigDecimal(body.get("initialBalance").toString());
            if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Invalid account type or initial balance.");
            }
            Account.AccountType accountType;
            try {
                accountType = Account.AccountType.valueOf(type);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid account type or initial balance.");
            }
            Account account = accountService.createAccount(userId, accountType, initialBalance);
            Map<String, Object> resp = new HashMap<>();
            resp.put("accountId", account.getId());
            resp.put("accountNumber", account.getAccountNumber());
            resp.put("message", "Account created successfully.");
            kafkaLogProducer.sendLog(toJson(resp), "Response");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid account type or initial balance."
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable UUID accountId) {
        kafkaLogProducer.sendLog(toJson(accountId), "Request");
        return accountService.getAccount(accountId)
            .map(account -> {
                Map<String, Object> resp = Map.of(
                    "accountId", account.getId(),
                    "accountNumber", account.getAccountNumber(),
                    "accountType", account.getAccountType(),
                    "balance", account.getBalance(),
                    "status", account.getStatus()
                );
                kafkaLogProducer.sendLog(toJson(resp), "Response");
                return ResponseEntity.ok(resp);
            })
            .orElseGet(() -> {
                Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "Account with ID " + accountId + " not found."
                );
                kafkaLogProducer.sendLog(toJson(error), "Response");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            });
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAccountsByUser(@PathVariable UUID userId) {
        kafkaLogProducer.sendLog(toJson(userId), "Request");
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        if (accounts.isEmpty()) {
            Map<String, Object> error = Map.of(
                "status", 404,
                "error", "Not Found",
                "message", "No accounts found for user ID " + userId + "."
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
        kafkaLogProducer.sendLog(toJson(resp), "Response");
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> body) {
        try {
            String reqJson = objectMapper.writeValueAsString(body);
            kafkaLogProducer.sendLog(reqJson, "Request");
            UUID fromAccountId = UUID.fromString((String) body.get("fromAccountId"));
            UUID toAccountId = UUID.fromString((String) body.get("toAccountId"));
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            accountService.transfer(fromAccountId, toAccountId, amount);
            String resJson = objectMapper.writeValueAsString(Map.of("message", "Account updated successfully."));
            kafkaLogProducer.sendLog(resJson, "Response");
            return ResponseEntity.ok(Map.of("message", "Account updated successfully."));
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            int status = 400;
            String error = "Bad Request";
            if (msg != null && msg.contains("not found")) {
                status = 404;
                error = "Not Found";
            }
            kafkaLogProducer.sendLog("{\"error\":\"Exception\",\"message\":\"" + msg + "\"}", "Response");
            return ResponseEntity.status(status).body(Map.of(
                "status", status,
                "error", error,
                "message", msg
            ));
        } catch (Exception e) {
            kafkaLogProducer.sendLog("{\"error\":\"Exception\",\"message\":\"" + e.getMessage() + "\"}", "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
            ));
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"JsonProcessingException\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }


    

} 