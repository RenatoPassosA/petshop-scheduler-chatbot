package com.project.petshop_scheduler_chatbot.application.petservices;

public interface UpdatePetServiceUseCase {
    PetServiceSummaryResult execute (UpdatePetServiceCommand service);
}
