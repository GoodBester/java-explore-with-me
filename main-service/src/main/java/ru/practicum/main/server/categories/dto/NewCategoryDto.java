package ru.practicum.main.server.categories.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String name;
}
