package com.virtualbankingsystem.account_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(UUID userId, Account.AccountType accountType, BigDecimal initialBalance) {
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountType(accountType);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(initialBalance);
        account.setStatus(Account.AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    public List<Account> getAccountsByUserId(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with ID " + fromAccountId + " not found."));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with ID " + toAccountId + " not found."));
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void inactivateStaleAccounts() {
        List<Account> activeAccounts = accountRepository.findAll().stream()
                .filter(a -> a.getStatus() == Account.AccountStatus.ACTIVE)
                .toList();
        for (Account account : activeAccounts) {
            if (Duration.between(account.getUpdatedAt(), LocalDateTime.now()).toHours() > 24) {
                account.setStatus(Account.AccountStatus.INACTIVE);
                accountRepository.save(account);
            }
        }
    }

    private String generateAccountNumber() {
        // Simple random 10-digit number, for demo only
        return String.valueOf((long)(Math.random() * 1_000_000_0000L));
    }
} 