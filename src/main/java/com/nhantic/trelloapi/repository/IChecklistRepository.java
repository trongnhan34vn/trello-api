package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IChecklistRepository extends JpaRepository<Checklist, UUID> {
    List<Checklist> findByCard_Id(UUID cardId);
}
