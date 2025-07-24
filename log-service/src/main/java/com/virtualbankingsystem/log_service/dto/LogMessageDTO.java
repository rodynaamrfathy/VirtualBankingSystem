package com.virtualbankingsystem.log_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogMessageDTO {
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
}
