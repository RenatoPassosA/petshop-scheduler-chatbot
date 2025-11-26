package com.project.petshop_scheduler_chatbot.application.petservices;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;

public interface PetServiceUseCase {
    AddPetServiceResult register (AddPetServiceCommand service);
    void update (Long id, UpdatePetServiceCommand service);
    List<PetService> getAll();
    PetService getPetService (Long id);
    void delete(Long id);
    
}
