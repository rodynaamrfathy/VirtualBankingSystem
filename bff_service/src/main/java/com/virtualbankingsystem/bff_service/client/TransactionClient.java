package com.virtualbankingsystem.bff_service.client;

import com.virtualbankingsystem.bff_service.dto.TransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "transaction-service", path = "/transactions")
public interface TransactionClient {
    @GetMapping("/accounts/{accountId}/transactions")
    List<TransactionDto> getTransactionsByAccount(@PathVariable UUID accountId);
}