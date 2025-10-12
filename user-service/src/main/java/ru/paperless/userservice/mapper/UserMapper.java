package ru.paperless.userservice.mapper;

import org.springframework.stereotype.Component;
import ru.paperless.userservice.model.DTO.UserCreateRequestDTO;
import ru.paperless.userservice.model.DTO.UserResponseDTO;
import ru.paperless.userservice.model.entity.UserEntity;

@Component
public class UserMapper {

    public UserResponseDTO toDto(UserEntity userEntity) {
        return new UserResponseDTO(
                userEntity.getId(),
                userEntity.getUserName(),
                userEntity.getEmail()
        );
    }

    public UserEntity toEntity(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null) {
            return null;
        }
        return UserEntity
                .builder()
                .userName(userCreateRequestDTO.userName())
                .email(userCreateRequestDTO.email())
                .build();
    }
}
