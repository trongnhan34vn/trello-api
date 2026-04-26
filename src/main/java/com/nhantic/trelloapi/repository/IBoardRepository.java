package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.Board;
import com.nhantic.trelloapi.repository.dto.BoardRecord;
import com.nhantic.trelloapi.repository.sql.BoardSql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IBoardRepository extends JpaRepository<Board, UUID> {
    @Query(value = BoardSql.FIND_BY_BOARD_ID, nativeQuery = true)
    List<BoardRecord> searchById(@Param("id") UUID id);
}
