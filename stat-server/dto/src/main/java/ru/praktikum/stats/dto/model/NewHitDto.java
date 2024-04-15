package ru.praktikum.stats.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class NewHitDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
