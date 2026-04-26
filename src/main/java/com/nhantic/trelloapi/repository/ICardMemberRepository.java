package com.nhantic.trelloapi.repository;

import com.nhantic.trelloapi.entity.CardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ICardMemberRepository extends JpaRepository<CardMember, UUID> {
}
