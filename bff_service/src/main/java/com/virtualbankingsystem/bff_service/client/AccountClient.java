package com.virtualbankingsystem.bff_service.client;

import com.virtualbankingsystem.bff_service.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "account-service", path = "/accounts")
public interface AccountClient {
    @GetMapping("/users/{userId}")
    List<AccountResponse> getAccountsByUser(@PathVariable UUID userId);
}


