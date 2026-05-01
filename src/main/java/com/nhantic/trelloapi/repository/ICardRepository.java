package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.Card;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ICardRepository extends JpaRepository<Card, UUID> {
    List<Card> findCardByListIdIn(Collection<UUID> listIds, Sort sort);
}
