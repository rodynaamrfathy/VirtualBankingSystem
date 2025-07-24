package com.virtualbankingsystem.transaction_service.service;

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

    public Transaction initiateTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.INITIATED);
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> executeTransfer(UUID transactionId, boolean success) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        transactionOpt.ifPresent(transaction -> {
            transaction.setStatus(success ? Transaction.TransactionStatus.SUCCESS : Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
        });
        return transactionOpt;
    }

    public List<Transaction> getTransactionsForAccount(UUID accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }
} 