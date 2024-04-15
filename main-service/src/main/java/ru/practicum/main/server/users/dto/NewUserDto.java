package ru.practicum.main.server.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class NewUserDto {
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
    @Size(min = 6, max = 254)
    @Email
    private String email;
}
