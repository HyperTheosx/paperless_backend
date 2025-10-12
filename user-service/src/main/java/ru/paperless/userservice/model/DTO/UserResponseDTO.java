package ru.paperless.userservice.model.DTO;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String userName,
        String email
) {

}
