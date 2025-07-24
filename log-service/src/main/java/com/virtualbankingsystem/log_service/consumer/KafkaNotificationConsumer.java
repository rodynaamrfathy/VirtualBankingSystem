package com.virtualbankingsystem.log_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualbankingsystem.log_service.dto.LogMessageDTO;
import com.virtualbankingsystem.log_service.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final LoggingService loggingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "log-topic", groupId = "logging-group")
    public void consume(String message) {
        try {
            LogMessageDTO logDto = objectMapper.readValue(message, LogMessageDTO.class);
            loggingService.saveLog(logDto);
            System.out.println("✅ Saved log: " + message);
        } catch (Exception e) {
            System.err.println("❌ Failed to consume message: " + message);
            e.printStackTrace();
        }
    }
}
