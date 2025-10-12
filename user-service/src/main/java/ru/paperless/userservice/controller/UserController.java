package ru.paperless.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.paperless.userservice.model.DTO.UserCreateRequestDTO;
import ru.paperless.userservice.model.DTO.UserResponseDTO;
import ru.paperless.userservice.model.DTO.UserUpdateRequestDTO;
import ru.paperless.userservice.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Управление пользователями", description = "API`s для управления пользователями системы")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Создание пользователя")
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid
            @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        UserResponseDTO newUser = userService.createUser(userCreateRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/users/" + newUser.id()))
                .body(newUser);
    }

    @PatchMapping(path = "/{id}")
    @Operation(summary = "Обновление данных пользователя")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateRequestDTO));
    }
}
