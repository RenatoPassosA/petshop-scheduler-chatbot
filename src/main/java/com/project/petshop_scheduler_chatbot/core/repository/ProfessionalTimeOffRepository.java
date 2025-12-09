package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;

public interface ProfessionalTimeOffRepository {
    boolean existsOverlap(Long professionalId, OffsetDateTime start, OffsetDateTime end);
    ProfessionalTimeOff save(ProfessionalTimeOff timeOff);
    Optional<ProfessionalTimeOff> findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<ProfessionalTimeOff> findAllByProfessionalId(Long professionalId);
}
