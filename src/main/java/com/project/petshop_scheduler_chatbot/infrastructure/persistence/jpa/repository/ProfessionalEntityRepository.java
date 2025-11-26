package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;

public interface ProfessionalEntityRepository extends JpaRepository<ProfessionalEntity, Long>{
}
