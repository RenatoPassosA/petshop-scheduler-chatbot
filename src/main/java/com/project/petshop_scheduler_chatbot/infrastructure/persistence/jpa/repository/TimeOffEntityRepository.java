package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;

public interface TimeOffEntityRepository extends JpaRepository<ProfessionalTimeOffEntity, Long>{
    List<ProfessionalTimeOffEntity> findAllByProfessionalId(Long professionalId);
}
