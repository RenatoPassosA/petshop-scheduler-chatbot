package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetEntity;

public interface PetEntityRepository extends JpaRepository<PetEntity, Long> {
    List<PetEntity> findAllByTutorId(Long tutorId);
    boolean existsByIdAndTutorId(Long petId, Long tutorId);
}
