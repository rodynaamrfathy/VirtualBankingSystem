package com.virtualbankingsystem.log_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table (name ="logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}
