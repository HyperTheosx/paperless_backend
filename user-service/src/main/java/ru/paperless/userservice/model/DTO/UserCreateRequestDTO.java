package ru.paperless.userservice.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDTO(

        @NotBlank
        @Size(min = 5, max = 30)
        String userName,

        @NotBlank
        @Email
        String email
) {
}
