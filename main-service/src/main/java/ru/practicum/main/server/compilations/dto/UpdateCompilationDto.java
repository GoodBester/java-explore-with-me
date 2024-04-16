package ru.practicum.main.server.compilations.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UpdateCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}