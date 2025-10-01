package com.project.petshop_scheduler_chatbot.application.petservices;

public interface RegisterPetServiceUseCase {
    PetServiceSummaryResult execute (RegisterPetServiceCommand service);
    
}
