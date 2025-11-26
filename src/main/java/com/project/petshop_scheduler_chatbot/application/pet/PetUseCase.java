package com.project.petshop_scheduler_chatbot.application.pet;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;

public interface PetUseCase {
    AddPetToTutorResult execute (AddPetToTutorCommand pet);
    void update(Long petId, UpdatePetCommand command);
    void delete(Long petId);
    Pet getPet(Long id);
    List<Pet> getAll();
}
