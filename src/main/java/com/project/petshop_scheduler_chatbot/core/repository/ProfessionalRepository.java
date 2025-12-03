package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;

public interface ProfessionalRepository {
    Professional save (Professional professional);
    Optional<Professional> findById(Long id);
    List<Professional> getAll();
    boolean existsById(Long id);
    void deleteById(Long id);
}
