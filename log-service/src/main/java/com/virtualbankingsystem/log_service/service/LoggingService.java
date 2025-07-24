package com.virtualbankingsystem.log_service.service;

import com.virtualbankingsystem.log_service.dto.LogEntryResponseDTO;
import com.virtualbankingsystem.log_service.dto.LogMessageDTO;
import com.virtualbankingsystem.log_service.model.Log;
import com.virtualbankingsystem.log_service.model.MessageType;
import com.virtualbankingsystem.log_service.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final LogRepository logRepository;

    public void saveLog(LogMessageDTO dto) {
        Log log = new Log();
        log.setMessage(dto.getMessage());
        log.setMessageType(MessageType.valueOf(dto.getMessageType().toUpperCase()));
        log.setDateTime(dto.getDateTime());
        logRepository.save(log);
    }

    public List<LogEntryResponseDTO> getAllLogs() {
        return logRepository.findAll().stream().map(log -> {
            LogEntryResponseDTO dto = new LogEntryResponseDTO();
            dto.setId(log.getId());
            dto.setMessage(log.getMessage());
            dto.setMessageType(log.getMessageType().name());
            dto.setDateTime(log.getDateTime());
            return dto;
        }).collect(Collectors.toList());
    }
}
