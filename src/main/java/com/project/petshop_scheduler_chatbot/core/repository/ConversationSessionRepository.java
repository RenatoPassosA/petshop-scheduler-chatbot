package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;

public interface ConversationSessionRepository {
    Optional<ConversationSession> findByWaId(String waId);
    void save(ConversationSession session);
    void deleteByWaId(String waId); 
}
