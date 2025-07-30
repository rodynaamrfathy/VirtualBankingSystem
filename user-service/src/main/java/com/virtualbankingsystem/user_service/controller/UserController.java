package com.virtualbankingsystem.user_service.controller;

import com.virtualbankingsystem.user_service.dto.*;
import com.virtualbankingsystem.user_service.producer.RequestLoggerProducer;
import com.virtualbankingsystem.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    private RequestLoggerProducer loggerProducer;

    @Autowired
    public UserController(UserService userService, RequestLoggerProducer loggerProducer) {
        this.userService = userService;
        this.loggerProducer = loggerProducer;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        // Hash password before logging
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Create a new request object with the hashed password (avoid logging the original one)
        RegisterRequest safeRequest = new RegisterRequest(
                request.getUsername(),
                hashedPassword,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );

        loggerProducer.logRequest(safeRequest); // Log request with hashed password

        RegisterResponse response = userService.registerUser(safeRequest); // Proceed with hashed password

        loggerProducer.logResponse(response); // Log the response
        return ResponseEntity.status(201).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody LoginRequest request) {
        loggerProducer.logRequest(request); // Log request

        UserLoginResponse response = userService.login(request);

        loggerProducer.logResponse(response); // Log response
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> getProfile(@PathVariable UUID userId) {
        loggerProducer.logRequest(Map.of("userId", userId)); // Log the path variable as request

        UserResponse response = userService.getUserProfile(userId);

        loggerProducer.logResponse(response); // Log the response
        return ResponseEntity.ok(response);
    }

}
