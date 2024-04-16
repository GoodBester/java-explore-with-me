package ru.prakticum.stats.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponce {
    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
