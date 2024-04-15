package ru.practicum.main.server.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}