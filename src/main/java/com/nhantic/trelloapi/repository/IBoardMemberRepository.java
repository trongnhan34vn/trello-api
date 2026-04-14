package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBoardMemberRepository extends JpaRepository<BoardMember, UUID> {
    Optional<BoardMember> findByUserIdAndBoardId(UUID userId, UUID boardId);
    boolean existsByUserIdAndBoardId(UUID userId, UUID boardId);
    List<BoardMember> findByBoardId(UUID boardId);
}
