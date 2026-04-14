package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCognitoId(String cognitoId);
    boolean existsByCognitoId(String cognitoId);
}
