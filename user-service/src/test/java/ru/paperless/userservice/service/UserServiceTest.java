package ru.paperless.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.paperless.userservice.exception.UserNotFoundException;
import ru.paperless.userservice.mapper.UserMapper;
import ru.paperless.userservice.model.DTO.UserCreateRequestDTO;
import ru.paperless.userservice.model.DTO.UserResponseDTO;
import ru.paperless.userservice.model.DTO.UserUpdateRequestDTO;
import ru.paperless.userservice.model.entity.UserEntity;
import ru.paperless.userservice.repository.UserRepository;

import java.util.List;
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

    private final UUID id = UUID.fromString("ef428bb1-e5ae-441d-afb6-e8f5b5e7670a");
    private final String userName = "admin";
    private final String email = "admin@example.com";

    protected final String TEST_VALID_USERNAME = "valid_username";
    protected final String TEST_VALID_EMAIL = "valid_email@example.com";

    private final String NOT_VALID_USERNAME = " ";
    private final String NOT_VALID_EMAIL = "not_valid_email";

    @Test
    void returnAllUsers_WhenExists() {
        UserEntity user1 = UserEntity.builder().id(id).userName(userName).email(email).build();
        UserEntity user2 = UserEntity.builder().id(UUID.randomUUID()).userName("user2").email("user2@example.com").build();
        UserEntity user3 = UserEntity.builder().id(UUID.randomUUID()).userName("user3").email("user3@example.com").build();

        UserResponseDTO dto1 = new UserResponseDTO(user1.getId(), user1.getUserName(), user1.getEmail());
        UserResponseDTO dto2 = new UserResponseDTO(user2.getId(), user2.getUserName(), user2.getEmail());
        UserResponseDTO dto3 = new UserResponseDTO(user3.getId(), user3.getUserName(), user3.getEmail());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);
        when(userMapper.toDto(user3)).thenReturn(dto3);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(userName, result.get(0).userName());
        assertEquals("user2", result.get(1).userName());
        assertEquals("user3", result.get(2).userName());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(3)).toDto(any(UserEntity.class));
    }

    @Test
    void returnAllUsers_WhenNotExists() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, never()).toDto(any());
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

        UserCreateRequestDTO createRequest = new UserCreateRequestDTO(TEST_VALID_USERNAME, TEST_VALID_EMAIL);

        UserEntity userEntityToSave = UserEntity.builder()
                .userName(TEST_VALID_USERNAME)
                .email(TEST_VALID_EMAIL)
                .build();

        UserEntity savedUserEntity = UserEntity.builder()
                .id(id)
                .userName(TEST_VALID_USERNAME)
                .email(TEST_VALID_EMAIL)
                .build();

        UserResponseDTO expectedResponse = new UserResponseDTO(
                id,
                TEST_VALID_USERNAME,
                TEST_VALID_EMAIL
        );

        when(userMapper.toEntity(createRequest)).thenReturn(userEntityToSave);
        when(userRepository.save(userEntityToSave)).thenReturn(savedUserEntity);
        when(userMapper.toDto(savedUserEntity)).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.createUser(createRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        assertEquals(TEST_VALID_USERNAME, actualResponse.userName());
        assertEquals(TEST_VALID_EMAIL, actualResponse.email());

        verify(userMapper, times(1)).toEntity(createRequest);
        verify(userRepository, times(1)).save(userEntityToSave);
        verify(userMapper, times(1)).toDto(savedUserEntity);
    }

    @Test
    void createUser_WithNotValidData() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(null)
        );

        assertEquals("User create request cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toEntity(any());
        verify(userMapper, never()).toDto(any());

    }

    @Test
    void updateUser_WithValidData() {

        UserEntity existingUser = UserEntity.builder()
                .id(id)
                .userName("old_username")
                .email("old@example.com")
                .build();

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO("new_username", null);
        UserEntity updatedUser = UserEntity.builder()
                .id(id)
                .userName("new_username")
                .email("old@example.com")
                .build();

        UserResponseDTO expectedResponse = new UserResponseDTO(id, "new_username", "old@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUser(id, updateRequest);

        assertNotNull(result);
        assertEquals("new_username", result.userName());
        assertEquals("old@example.com", result.email()); // email не изменился

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    void updateUser_WithNotValidData() {

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO("new_username", "new@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(id, updateRequest)
        );

        assertEquals("Пользователь с ID: " + id + " не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateUser_UpdateOnlyEmail() {

        UserEntity existingUser = UserEntity.builder()
                .id(id)
                .userName("username")
                .email("old@example.com")
                .build();

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO(null, "new@example.com");

        UserEntity updatedUser = UserEntity.builder()
                .id(id)
                .userName("username")
                .email("new@example.com")
                .build();

        UserResponseDTO expectedResponse = new UserResponseDTO(id, "username", "new@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUser(id, updateRequest);

        assertNotNull(result);
        assertEquals("username", result.userName());
        assertEquals("new@example.com", result.email());
    }
}