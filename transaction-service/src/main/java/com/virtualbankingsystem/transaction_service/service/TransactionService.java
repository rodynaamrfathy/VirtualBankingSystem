package com.virtualbankingsystem.transaction_service.service;

import com.virtualbankingsystem.transaction_service.client.AccountClient;
import com.virtualbankingsystem.transaction_service.dto.*;
import com.virtualbankingsystem.transaction_service.model.Transaction;
import com.virtualbankingsystem.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountClient accountClient;

    public TransferInitiationResponse initiateTransfer(TransferInitiationRequest request) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.TransactionStatus.INITIATED);
        transaction = transactionRepository.save(transaction);

        TransferInitiationResponse response = new TransferInitiationResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setStatus(transaction.getStatus().name());
        response.setTimestamp(transaction.getTimestamp()); // assuming `timestamp` is in entity
        return response;
    }

    public TransactionResponse executeTransfer(TransferExecutionRequest request, boolean success) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(request.getTransactionId());
        if (transactionOpt.isEmpty()) return null;

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(success ? Transaction.TransactionStatus.SUCCESS : Transaction.TransactionStatus.FAILED);
        transaction = transactionRepository.save(transaction);

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setStatus(transaction.getStatus().name());
        response.setTimestamp(transaction.getTimestamp());
        return response;
    }

    public TransactionResponse processTransfer(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (transaction.getStatus() != Transaction.TransactionStatus.INITIATED) {
            throw new IllegalStateException("Transaction already processed or invalid state");
        }

        // Build the account service transfer request
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(transaction.getFromAccountId());
        transferRequest.setToAccountId(transaction.getToAccountId());
        transferRequest.setAmount(transaction.getAmount());
        transferRequest.setDescription(transaction.getDescription());

        try {
            accountClient.transfer(transferRequest); // This will throw if failed
            transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            throw new RuntimeException("Account transfer failed: " + e.getMessage(), e);
        }

        transactionRepository.save(transaction);

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setStatus(transaction.getStatus().name());
        response.setTimestamp(transaction.getTimestamp());

        return response;
    }

    public List<TransactionHistoryResponse> getTransactionsForAccount(UUID accountId) {
        return transactionRepository
                .findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(tx -> {
                    TransactionHistoryResponse response = new TransactionHistoryResponse();
                    response.setAccountId(accountId);
                    response.setAmount(tx.getAmount());
                    response.setDescription(tx.getDescription());
                    return response;
                })
                .toList();
    }
}
