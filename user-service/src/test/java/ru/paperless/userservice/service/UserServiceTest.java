package ru.paperless.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.paperless.userservice.exception.UserNotFoundException;
import ru.paperless.userservice.mapper.UserMapper;
import ru.paperless.userservice.model.DTO.UserResponseDTO;
import ru.paperless.userservice.model.entity.UserEntity;
import ru.paperless.userservice.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private final UUID id = UUID.fromString("ef428bb1-e5ae-441d-afb6-e8f5b5e7655f");
    private final String userName = "mega_admin";
    private final String email = "admin@example.com";

    @Test
    void returnAllUsers_WhenExists() {

    }

    @Test
    void returnAllUsers_WhenNotExists() {

    }

    @Test
    void returnExistingUserById() {
        UserEntity userEntity = UserEntity.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .build();

        UserResponseDTO expectedResponse = new UserResponseDTO(id, userName, email);

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.getUserById(id);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(id, actualResponse.id());
        assertEquals(userName, actualResponse.userName());
        assertEquals(email, actualResponse.email());

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toDto(userEntity);
    }

    @Test
    void returnNotExistingUserById() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.getUserById(id)
        );

        assertEquals("Пользователь с ID: " + id + " не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_WithValidData() {

    }

    @Test
    void createUser_WithNotValidData() {

    }

    @Test
    void updateUser_WithValidData() {

    }

    @Test
    void updateUser_WithNotValidData() {

    }
}