package com.virtualbankingsystem.transaction_service.client;

import com.virtualbankingsystem.transaction_service.dto.AccountResponse;
import com.virtualbankingsystem.transaction_service.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClient {

    @GetMapping("/{accountId}")
    AccountResponse getAccountById(@PathVariable("accountId") UUID accountId);

    @PutMapping("/transfer")
    ResponseEntity<?> transfer(@RequestBody TransferRequest transferRequest);

    @PostMapping("/internal/transfer")
    boolean transfer(@RequestBody UUID transactionId); // adjust if needed
}


