package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;

public interface ProfessionalTimeOffEntityRepository extends JpaRepository<ProfessionalTimeOffEntity, Long> {
    boolean existsByProfessional_IdAndStartAtLessThanAndEndAtGreaterThan(Long professionalId, OffsetDateTime end, OffsetDateTime start);
    List<ProfessionalTimeOffEntity> findByProfessionalId(Long professionalId);
}
