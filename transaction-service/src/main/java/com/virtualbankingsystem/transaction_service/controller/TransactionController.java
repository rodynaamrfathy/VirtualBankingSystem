package com.virtualbankingsystem.transaction_service.controller;

import com.virtualbankingsystem.transaction_service.model.Transaction;
import com.virtualbankingsystem.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody Map<String, Object> request) {
        try {
            UUID fromAccountId = UUID.fromString((String) request.get("fromAccountId"));
            UUID toAccountId = UUID.fromString((String) request.get("toAccountId"));
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");
            Transaction transaction = transactionService.initiateTransfer(fromAccountId, toAccountId, amount, description);
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transaction.getTransactionId());
            response.put("status", transaction.getStatus());
            response.put("timestamp", transaction.getTimestamp());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid 'from' or 'to' account ID."
            ));
        }
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody Map<String, Object> request) {
        try {
            UUID transactionId = UUID.fromString((String) request.get("transactionId"));
            // Here, you would call Account Service to debit/credit accounts and check for success
            boolean success = true; // Placeholder for actual logic
            Optional<Transaction> transactionOpt = transactionService.executeTransfer(transactionId, success);
            if (transactionOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("transactionId", transaction.getTransactionId());
                response.put("status", transaction.getStatus());
                response.put("timestamp", transaction.getTimestamp());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", "Invalid transaction ID."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid transaction ID."
            ));
        }
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactionsForAccount(@PathVariable String accountId) {
        try {
            UUID uuid = UUID.fromString(accountId);
            List<Transaction> transactions = transactionService.getTransactionsForAccount(uuid);
            if (transactions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No transactions found for account ID " + accountId + "."
                ));
            }
            List<Map<String, Object>> response = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            for (Transaction t : transactions) {
                Map<String, Object> tx = new HashMap<>();
                tx.put("transactionId", t.getTransactionId());
                tx.put("accountId", accountId);
                tx.put("amount", t.getFromAccountId().equals(uuid) ? t.getAmount().negate() : t.getAmount());
                tx.put("description", t.getDescription());
                tx.put("timestamp", t.getTimestamp().format(formatter));
                response.add(tx);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", "No transactions found for account ID " + accountId + "."
            ));
        }
    }
} 