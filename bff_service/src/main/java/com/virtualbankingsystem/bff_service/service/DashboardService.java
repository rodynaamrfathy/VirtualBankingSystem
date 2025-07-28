package com.virtualbankingsystem.bff_service.service;

import com.virtualbankingsystem.bff_service.client.AccountClient;
import com.virtualbankingsystem.bff_service.client.TransactionClient;
import com.virtualbankingsystem.bff_service.client.UserClient;
import com.virtualbankingsystem.bff_service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserClient userClient;
    private final AccountClient accountClient;
    private final TransactionClient transactionClient;

    public DashboardResponse getDashboard(UUID userId) {
        // Step 1: Get user info
        UserResponse user = userClient.getUser(userId);

        // Step 2: Get accounts for user
        List<AccountResponse> accounts = accountClient.getAccountsByUser(userId);

        // Step 3: For each account, get transactions
        List<AccountWithTransactions> detailedAccounts = accounts.stream().map(account -> {
            List<TransactionDto> transactions = transactionClient.getTransactionsByAccount(account.getAccountId());
            return AccountWithTransactions.builder()
                    .accountId(account.getAccountId())
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType())
                    .balance(account.getBalance())
                    .transactions(transactions)
                    .build();
        }).collect(Collectors.toList());

        return DashboardResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accounts(detailedAccounts)
                .build();
    }
}

