package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.repository.ConversationSessionRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ConversationSessionEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.ConversationSessionMapper;

@Repository
public class ConversationSessionRepositoryJpa implements ConversationSessionRepository {

    private final ConversationSessionEntityRepository conversationSessionEntityRepository;

    public ConversationSessionRepositoryJpa(ConversationSessionEntityRepository conversationSessionEntityRepository) {
        this.conversationSessionEntityRepository = conversationSessionEntityRepository;
    }

    @Override
    public Optional<ConversationSession> findByWaId(String waId) {
        return conversationSessionEntityRepository
                .findById(waId)
                .map(ConversationSessionMapper::toDomain);
    }

    @Override
    public ConversationSession save(ConversationSession session) {
        ConversationSessionEntity saved = conversationSessionEntityRepository.save(ConversationSessionMapper.toEntity(session));
        return ConversationSessionMapper.toDomain(saved);
    }
}