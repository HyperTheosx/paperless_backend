package ru.paperless.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.paperless.userservice.model.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    List<UserEntity> id(UUID id);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);
}
