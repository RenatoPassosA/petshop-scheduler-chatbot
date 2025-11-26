package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetServiceEntity;

public interface PetServiceEntityRepository extends JpaRepository <PetServiceEntity, Long> {
    List<PetServiceEntity> findByName(String name);
    @Query("select p.duration from PetServiceEntity p where p.id = :id")
    Optional<Integer> findDurationById(@Param("id") Long id);
}



