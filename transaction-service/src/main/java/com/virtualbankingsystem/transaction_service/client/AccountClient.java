package com.virtualbankingsystem.transaction_service.client;

import com.virtualbankingsystem.transaction_service.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountClient {
    @PostMapping("/accounts/internal/transfer")
    boolean transfer(@RequestBody UUID transactionId);

    @PutMapping("/transfer")
    ResponseEntity<?> transfer(@RequestBody TransferRequest transferRequest);


}

