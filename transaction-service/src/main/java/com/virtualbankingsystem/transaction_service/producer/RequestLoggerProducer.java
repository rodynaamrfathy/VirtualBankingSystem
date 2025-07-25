package com.virtualbankingsystem.transaction_service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class RequestLoggerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RequestLoggerProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void logRequest(Object jsonRequest) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("message", objectMapper.writeValueAsString(jsonRequest));
            message.put("messageType", "Request");
            message.put("dateTime", LocalDateTime.now().toString());

            kafkaTemplate.send("log-topic", objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logResponse(Object jsonResponse) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("message", objectMapper.writeValueAsString(jsonResponse));
            message.put("messageType", "Response");
            message.put("dateTime", LocalDateTime.now().toString());

            kafkaTemplate.send("log-topic", objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}