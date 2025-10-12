package ru.paperless.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.paperless.userservice.exception.EmailAlreadyExistsException;
import ru.paperless.userservice.exception.UserNotFoundException;
import ru.paperless.userservice.exception.UserNameAlreadyExistsException;
import ru.paperless.userservice.mapper.UserMapper;
import ru.paperless.userservice.model.DTO.UserCreateRequestDTO;
import ru.paperless.userservice.model.DTO.UserResponseDTO;
import ru.paperless.userservice.model.DTO.UserUpdateRequestDTO;
import ru.paperless.userservice.model.entity.UserEntity;
import ru.paperless.userservice.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserResponseDTO getUserById(UUID id) {
        return userMapper.toDto(getUserEntityById(id));
    }

    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        userCreateRequestValidation(userCreateRequestDTO);
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userCreateRequestDTO)));
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateRequestDTO userUpdateRequestDTO) {
        UserEntity user = getUserEntityById(id);

        if (userUpdateRequestDTO.userName() != null &&
                !user.getUserName().equals(userUpdateRequestDTO.userName()) &&
                userRepository.existsByUserName(userUpdateRequestDTO.userName())) {
            throw new UserNameAlreadyExistsException("Уже существует пользователь с username: " + userUpdateRequestDTO.userName());
        }

        if (userUpdateRequestDTO.email() != null &&
                !user.getEmail().equals(userUpdateRequestDTO.email()) &&
                userRepository.existsByEmail(userUpdateRequestDTO.email())) {
            throw new EmailAlreadyExistsException("Уже существует пользователь с email: " + userUpdateRequestDTO.email());
        }

        if (userUpdateRequestDTO.userName() != null) {
            user.setUserName(userUpdateRequestDTO.userName());
        }
        if (userUpdateRequestDTO.email() != null) {
            user.setEmail(userUpdateRequestDTO.email());
        }
        return userMapper.toDto(userRepository.save(user));
    }

    private UserEntity getUserEntityById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + id + " не найден"));
    }

    private void userCreateRequestValidation(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null) {
            throw new IllegalArgumentException("User create request cannot be null");
        }
        if (userRepository.existsByUserName(userCreateRequestDTO.userName())) {
            throw new UserNameAlreadyExistsException("Уже существует пользователь с username: " + userCreateRequestDTO.userName());
        }
        if (userRepository.existsByEmail(userCreateRequestDTO.email())) {
            throw new EmailAlreadyExistsException("Уже существует пользователь с email: " + userCreateRequestDTO.email());
        }
    }
}
