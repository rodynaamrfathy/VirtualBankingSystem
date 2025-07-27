package com.virtualbankingsystem.bff_service.service;

import com.virtualbankingsystem.bff_service.dto.AccountResponse;
import com.virtualbankingsystem.bff_service.dto.DashboardResponse;
import com.virtualbankingsystem.bff_service.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Optional<Object> getDashboardData(UUID userId) {
    }
}

