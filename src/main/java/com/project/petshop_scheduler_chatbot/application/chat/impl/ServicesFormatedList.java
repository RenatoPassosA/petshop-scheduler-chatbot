package com.project.petshop_scheduler_chatbot.application.chat.impl;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

public class ServicesFormatedList {

    private PetServiceRepository petServiceRepository;

    public ServicesFormatedList(PetServiceRepository petServiceRepository) {
        this.petServiceRepository = petServiceRepository;
    }

    public String getAllServicesFormated() {
        List<PetService> services = petServiceRepository.getAll();
        String servicesList = new String();

        for (PetService petService : services) {
            servicesList += petService.getName() + " - R$ " + petService.getPrice() + "\n";
        }
        return servicesList;
    }
}
