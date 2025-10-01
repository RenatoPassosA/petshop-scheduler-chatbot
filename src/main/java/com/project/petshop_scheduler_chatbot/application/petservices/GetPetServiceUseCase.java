package com.project.petshop_scheduler_chatbot.application.petservices;

public interface GetPetServiceUseCase {
    PetServiceSummaryResult execute (Long id);
}
