package com.virtualbankingsystem.account_service;

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
            Account.AccountType accountType = Account.AccountType.valueOf(type);
            Account account = accountService.createAccount(userId, accountType, BigDecimal.ZERO);
            Map<String, Object> resp = new HashMap<>();
            resp.put("accountId", account.getId());
            resp.put("accountNumber", account.getAccountNumber());
            resp.put("message", "Account created successfully.");
            kafkaLogProducer.sendLog(toJson(resp), "Response");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
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
                    "message", "Account not found."
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
        } catch (JsonProcessingException e) {
            kafkaLogProducer.sendLog("{\"error\":\"JsonProcessingException\",\"message\":\"" + e.getMessage() + "\"}", "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage()
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