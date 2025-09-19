package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;

public interface PetRepository {
    Pet save (Pet pet);
    Optional<Pet> findById(Long id);
    List<Pet> listByTutor(Long tutorId);
}
