package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBoardMemberRepository extends JpaRepository<BoardMember, UUID> {
    Optional<BoardMember> findByUserIdAndBoardId(UUID userId, UUID boardId);

    boolean existsByUserIdAndBoardId(UUID userId, UUID boardId);

    @Query("""
                SELECT bm
                FROM BoardMember bm
                WHERE bm.board.id = :boardId
                  AND (
                    LOWER(bm.user.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(bm.user.email) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    List<BoardMember> searchBoardMembersByBoardId(@Param("boardId") UUID boardId, @Param("search") String search);

    List<BoardMember> findByBoard_Id(UUID boardId);
}
