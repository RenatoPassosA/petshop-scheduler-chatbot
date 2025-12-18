package com.project.petshop_scheduler_chatbot.application.chat.impl.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Component
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

    public ProcessIncomingMessageResult sendServicesList() {
        return ProcessIncomingMessageResult.text(getAllServicesFormated());
    }
}
