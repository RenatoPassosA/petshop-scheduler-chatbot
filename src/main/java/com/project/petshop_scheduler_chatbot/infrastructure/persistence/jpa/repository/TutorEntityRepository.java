package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.TutorEntity;

public interface TutorEntityRepository extends JpaRepository<TutorEntity, Long>{
    Optional<TutorEntity> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
