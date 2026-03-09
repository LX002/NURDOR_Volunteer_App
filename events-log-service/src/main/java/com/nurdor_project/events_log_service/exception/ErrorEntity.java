package com.nurdor_project.events_log_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorEntity {
    private String message;
    private LocalDateTime timestamp;
}
