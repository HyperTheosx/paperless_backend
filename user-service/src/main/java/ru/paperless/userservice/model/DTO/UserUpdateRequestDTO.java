package ru.paperless.userservice.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(

        @Size(min = 5, max = 30)
        String userName,

        @Email
        String email
) {
}
