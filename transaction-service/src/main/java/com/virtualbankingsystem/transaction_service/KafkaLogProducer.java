package com.virtualbankingsystem.transaction_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaLogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public KafkaLogProducer(KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${logging.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendLog(String message, String messageType) {
        Map<String, Object> log = new HashMap<>();
        log.put("message", message);
        log.put("messageType", messageType);
        log.put("dateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        String json = new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(log).toString();
        kafkaTemplate.send(topic, json);
    }
} 