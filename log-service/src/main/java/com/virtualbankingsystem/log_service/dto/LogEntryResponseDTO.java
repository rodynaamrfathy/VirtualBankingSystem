package com.virtualbankingsystem.log_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogEntryResponseDTO {
    private Long id;
    private String message;
    private String messageType;
    private LocalDateTime dateTime;
}
