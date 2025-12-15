package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ConversationSessionEntity;

public interface ConversationSessionEntityRepository extends JpaRepository<ConversationSessionEntity, String> {
}