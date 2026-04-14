package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.constant.RoleName;
import com.nhantic.trelloapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}
