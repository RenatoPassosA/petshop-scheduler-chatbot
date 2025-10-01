package com.project.petshop_scheduler_chatbot.application.petservices.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.petservices.GetPetServiceUseCase;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceSummaryResult;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultGetPetServiceUseCase implements GetPetServiceUseCase{
    private final PetServiceRepository petServiceRepository;

    public DefaultGetPetServiceUseCase(PetServiceRepository petServiceRepository) {
        this.petServiceRepository = petServiceRepository;
    }

    @Override
    public PetServiceSummaryResult execute (Long id) {
        PetService petService = loadExistingService(id);
        PetServiceSummaryResult result = new PetServiceSummaryResult(petService.getId(),
                                                                petService.getName(),
                                                                petService.getPrice(),
                                                                petService.getDuration(),
                                                                petService.getCreatedAt(),
                                                                petService.getUpdatedAt());

        return (result);
    }

    private PetService loadExistingService(Long id) {
    
        if (id == null || id <= 0)
            throw new DomainValidationException("Id inválido");
        Optional<PetService> findService = petServiceRepository.findById(id);
        if (findService.isEmpty()) 
            throw new ServiceNotFoundException("Serviço não encontrado");
        return (findService.get());
    }
}
