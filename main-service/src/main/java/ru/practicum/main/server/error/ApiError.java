package ru.practicum.main.server.error;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
