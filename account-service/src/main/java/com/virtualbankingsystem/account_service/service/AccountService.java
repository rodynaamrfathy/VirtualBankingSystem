package com.virtualbankingsystem.account_service.service;

import com.virtualbankingsystem.account_service.client.TransactionClient;
import com.virtualbankingsystem.account_service.client.UserClient;
import com.virtualbankingsystem.account_service.dto.AccountResponse;
import com.virtualbankingsystem.account_service.dto.CreateAccountRequest;
import com.virtualbankingsystem.account_service.dto.TransactionDto;
import com.virtualbankingsystem.account_service.dto.TransferRequest;
import com.virtualbankingsystem.account_service.repository.AccountRepository;
import com.virtualbankingsystem.account_service.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final TransactionClient transactionClient;


    public AccountResponse createAccount(CreateAccountRequest request) {
        // Validate user
        userClient.getUser(request.getUserId());

        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountType(Account.AccountType.valueOf(request.getAccountType()));
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(request.getInitialBalance());
        account.setStatus(Account.AccountStatus.ACTIVE);

        Account saved = accountRepository.save(account);
        return AccountResponse.builder()
                .accountId(saved.getId())
                .accountNumber(saved.getAccountNumber())
                .accountType(saved.getAccountType().name())
                .balance(saved.getBalance())
                .status(saved.getStatus().name())
                .build();
    }

    public Optional<Account> getAccount(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    public List<AccountResponse> getAccountsByUser(UUID userId) {
        return accountRepository.findByUserId(userId).stream().map(acc -> AccountResponse.builder()
                .accountId(acc.getId())
                .accountNumber(acc.getAccountNumber())
                .accountType(acc.getAccountType().name())
                .balance(acc.getBalance())
                .status(acc.getStatus().name())
                .build()
        ).toList();
    }

    @Transactional
    public void transfer(TransferRequest request) {
        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);
    }

    private String generateAccountNumber() {
        return String.format("%010d", new Random().nextInt(1_000_000_000));
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    @Transactional
    public void inactivateStaleAccounts() {
        List<Account> activeAccounts = accountRepository.findByStatus(Account.AccountStatus.ACTIVE);

        for (Account account : activeAccounts) {
            List<TransactionDto> transactions = transactionClient.getTransactionsByAccount(account.getId());

            // If no transactions, skip (or optionally handle separately)
            if (transactions.isEmpty()) continue;

            // Find latest transaction timestamp
            LocalDateTime latest = transactions.stream()
                    .map(TransactionDto::getTimestamp)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (latest == null) continue;

            // Check if more than 24 hours passed
            if (Duration.between(latest, LocalDateTime.now()).toHours() > 24) {
                account.setStatus(Account.AccountStatus.INACTIVE);
                accountRepository.save(account);
            }
        }
    }

}
