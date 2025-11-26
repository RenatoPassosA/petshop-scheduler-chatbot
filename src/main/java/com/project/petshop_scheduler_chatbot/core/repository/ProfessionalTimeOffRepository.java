package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.OffsetDateTime;

public interface ProfessionalTimeOffRepository {
    boolean existsOverlap(Long professionalId, java.time.OffsetDateTime start, java.time.OffsetDateTime end);
    void save(Long professionalId, OffsetDateTime start, OffsetDateTime end, String reason, OffsetDateTime createdAt);
    boolean existsById(Long id);
    void deleteById(Long id);
}
