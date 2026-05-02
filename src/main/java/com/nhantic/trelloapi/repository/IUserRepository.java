package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCognitoId(String cognitoId);

    boolean existsByCognitoId(String cognitoId);

    List<User> findUsersByIdIn(Collection<UUID> ids);

    @Query(
            value = """
                        SELECT u
                        FROM User u
                        WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    """
    )
    List<User> searchAll(@Param("search") String search);
}
