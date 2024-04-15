package ru.prakticum.stats.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponce {
    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
