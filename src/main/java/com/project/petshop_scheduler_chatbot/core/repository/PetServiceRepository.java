package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;

public interface PetServiceRepository {
    PetService save (PetService service);
    Optional<PetService> findById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<PetService> findByName(String name);
    List<PetService> getAll();
    int durationById(Long id);
}
