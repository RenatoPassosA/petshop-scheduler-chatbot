package com.project.petshop_scheduler_chatbot.application.petservices.impl;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceSummaryResult;
import com.project.petshop_scheduler_chatbot.application.petservices.RegisterPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.RegisterPetServiceUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultRegisterPetServiceUseCase implements RegisterPetServiceUseCase {
    final private PetServiceRepository petServiceRepository;
    final private TimeProvider timeProvider;
    
    public DefaultRegisterPetServiceUseCase (PetServiceRepository petServiceRepository, TimeProvider timeProvider) {
        this.petServiceRepository = petServiceRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public PetServiceSummaryResult execute (RegisterPetServiceCommand service) {
        validations(service);
        PetService petService = new PetService(service.getName(),
                                            service.getPrice(),
                                            service.getDuration(),
                                            timeProvider.nowInUTC(),
                                            timeProvider.nowInUTC());
        petServiceRepository.save(petService);

        PetServiceSummaryResult result = new PetServiceSummaryResult(petService.getId(),
                                                                petService.getName(),
                                                                petService.getPrice(),
                                                                petService.getDuration(),
                                                                petService.getCreatedAt(),
                                                                petService.getUpdatedAt());

        return (result);
    }

    private void validations(RegisterPetServiceCommand service) {

        int scheduleStep = 15;

        if (service.getName() == null || service.getName().isBlank())
            throw new ServiceNotFoundException("Nome do Serviço é obrigatório");
        if (service.getDuration() < 30 || service.getDuration() > 180)
            throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
        if (service.getDuration() % scheduleStep != 0)
            throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
    }
}
