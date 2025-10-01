package com.project.petshop_scheduler_chatbot.application.petservices.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.petservices.ListPetServicesUseCase;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceSummaryResult;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultListServicesUseCase implements ListPetServicesUseCase {

    private final PetServiceRepository petServiceRepository;

    public DefaultListServicesUseCase(PetServiceRepository petServiceRepository) {
        this.petServiceRepository = petServiceRepository;
    }

    @Override
    public List<PetServiceSummaryResult> listAll() {

        List<PetServiceSummaryResult> returnList = new ArrayList<>();
        List<PetService> servicesList = petServiceRepository.findAll();
        PetServiceSummaryResult result;

        for (int index = 0; index < servicesList.size(); index++) {
            PetService petService = servicesList.get(index);
            result = new PetServiceSummaryResult(
                petService.getId(),
                petService.getName(),
                petService.getPrice(),
                petService.getDuration(),
                petService.getCreatedAt(),
                petService.getUpdatedAt()
            );
            returnList.add(result);
    
        }
        return (returnList);
    }
}
