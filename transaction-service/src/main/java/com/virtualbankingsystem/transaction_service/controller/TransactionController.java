package com.virtualbankingsystem.transaction_service.controller;

import com.virtualbankingsystem.transaction_service.model.Transaction;
import com.virtualbankingsystem.transaction_service.service.TransactionService;
import com.virtualbankingsystem.transaction_service.KafkaLogProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private KafkaLogProducer kafkaLogProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"JsonProcessingException\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody Map<String, Object> request) {
        kafkaLogProducer.sendLog(toJson(request), "Request");
        try {
            UUID fromAccountId = UUID.fromString((String) request.get("fromAccountId"));
            UUID toAccountId = UUID.fromString((String) request.get("toAccountId"));
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            Transaction transaction = transactionService.initiateTransfer(fromAccountId, toAccountId, amount, description);
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transaction.getTransactionId());
            response.put("status", "Initiated");
            response.put("timestamp", transaction.getTimestamp());
            kafkaLogProducer.sendLog(toJson(response), "Response");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid 'from' or 'to' account ID."
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody Map<String, Object> request) {
        kafkaLogProducer.sendLog(toJson(request), "Request");
        try {
            UUID transactionId = UUID.fromString((String) request.get("transactionId"));
            // Here, you should call Account Service to actually perform the transfer and check for errors.
            // For now, we assume success if the transaction exists.
            Optional<Transaction> transactionOpt = transactionService.executeTransfer(transactionId, true);
            if (transactionOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("transactionId", transaction.getTransactionId());
                response.put("status", transaction.getStatus().name().substring(0, 1).toUpperCase() + transaction.getStatus().name().substring(1).toLowerCase());
                response.put("timestamp", transaction.getTimestamp());
                kafkaLogProducer.sendLog(toJson(response), "Response");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", "Invalid transaction ID."
                );
                kafkaLogProducer.sendLog(toJson(error), "Response");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Invalid transaction ID."
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactionsForAccount(@PathVariable String accountId) {
        kafkaLogProducer.sendLog(toJson(accountId), "Request");
        try {
            UUID uuid = UUID.fromString(accountId);
            List<Transaction> transactions = transactionService.getTransactionsForAccount(uuid);
            if (transactions.isEmpty()) {
                Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No transactions found for account ID " + accountId + "."
                );
                kafkaLogProducer.sendLog(toJson(error), "Response");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
            kafkaLogProducer.sendLog(toJson(response), "Response");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", 404,
                "error", "Not Found",
                "message", "No transactions found for account ID " + accountId + "."
            );
            kafkaLogProducer.sendLog(toJson(error), "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
} 