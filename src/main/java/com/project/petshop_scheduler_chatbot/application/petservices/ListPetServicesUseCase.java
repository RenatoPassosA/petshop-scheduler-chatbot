package com.project.petshop_scheduler_chatbot.application.petservices;

import java.util.List;

public interface ListPetServicesUseCase {
    List<PetServiceSummaryResult> listAll();
}
