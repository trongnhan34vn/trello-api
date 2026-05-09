package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.CardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ICardMemberRepository extends JpaRepository<CardMember, UUID> {
    List<CardMember> findByCard_Id(UUID cardId);
}
