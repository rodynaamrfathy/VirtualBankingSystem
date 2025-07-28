package com.virtualbankingsystem.bff_service.client;

import com.virtualbankingsystem.bff_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/{userId}/profile")
    UserResponse getUser(@PathVariable UUID userId);
}

