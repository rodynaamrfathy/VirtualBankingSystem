package com.virtualbankingsystem.transaction_service.service;

import com.virtualbankingsystem.transaction_service.client.AccountClient;
import com.virtualbankingsystem.transaction_service.dto.*;
import com.virtualbankingsystem.transaction_service.model.Transaction;
import com.virtualbankingsystem.transaction_service.model.Transaction.TransactionStatus;
import com.virtualbankingsystem.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    public ResponseEntity<?> initiateTransfer(TransferInitiationRequest request) {
        try {
            AccountResponse fromAccount = accountClient.getAccountById(request.getFromAccountId());
            AccountResponse toAccount = accountClient.getAccountById(request.getToAccountId());

            if (fromAccount == null || toAccount == null) {
                return badRequest("Invalid 'from' or 'to' account ID.");
            }

            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                return badRequest("Insufficient funds.");
            }

            Transaction transaction = new Transaction();
            transaction.setFromAccountId(request.getFromAccountId());
            transaction.setToAccountId(request.getToAccountId());
            transaction.setAmount(request.getAmount());
            transaction.setDescription(request.getDescription());
            transaction.setStatus(TransactionStatus.INITIATED);

            transaction = transactionRepository.save(transaction);

            TransferInitiationResponse response = new TransferInitiationResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setStatus("Initiated");
            response.setTimestamp(transaction.getTimestamp());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return internalError(e);
        }
    }

    public ResponseEntity<?> executeTransfer(TransferExecutionRequest request) {
        Optional<Transaction> optional = transactionRepository.findById(request.getTransactionId());

        if (optional.isEmpty()) {
            return badRequest("Transaction ID not found.");
        }

        Transaction transaction = optional.get();

        try {
            AccountResponse fromAccount = accountClient.getAccountById(transaction.getFromAccountId());
            AccountResponse toAccount = accountClient.getAccountById(transaction.getToAccountId());

            if (fromAccount == null || toAccount == null) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                return badRequest("Invalid 'from' or 'to' account ID.");
            }

            if (fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                return badRequest("Insufficient funds.");
            }

            TransferRequest transferRequest = new TransferRequest(
                    transaction.getFromAccountId(),
                    transaction.getToAccountId(),
                    transaction.getAmount(),
                    "EGP",
                    transaction.getDescription()
            );

            accountClient.transfer(transferRequest); // assumes transfer will throw exception if failed

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            TransactionResponse response = new TransactionResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setStatus("Success");
            response.setTimestamp(transaction.getTimestamp());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            return internalError(e);
        }
    }

    public ResponseEntity<?> getTransactionsForAccount(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);

        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No transactions found for account ID " + accountId
            ));
        }

        List<TransactionHistoryResponse> responseList = transactions.stream().map(tx -> {
            BigDecimal signedAmount = accountId.equals(tx.getFromAccountId())
                    ? tx.getAmount().negate()
                    : tx.getAmount();

            TransactionHistoryResponse response = new TransactionHistoryResponse();
            response.setAccountId(accountId);
            response.setAmount(signedAmount);
            response.setDescription(tx.getDescription());
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    // Helpers
    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", message
        ));
    }

    private ResponseEntity<?> internalError(Exception e) {
        return ResponseEntity.status(500).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", e.getMessage()
        ));
    }
}
