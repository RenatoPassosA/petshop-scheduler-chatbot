package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalWorkingHoursEntity;

public interface WorkingHoursEntityRepository extends JpaRepository<ProfessionalWorkingHoursEntity, Long>{
    List<ProfessionalWorkingHoursEntity> findAllByProfessionalId(Long professionalId);
}
