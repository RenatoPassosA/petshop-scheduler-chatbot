package com.project.petshop_scheduler_chatbot.application.petservices.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceSummaryResult;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultUpdatePetServiceUseCase implements UpdatePetServiceUseCase {
    final private PetServiceRepository petServiceRepository;
    final private TimeProvider timeProvider;
    
    public DefaultUpdatePetServiceUseCase (PetServiceRepository petServiceRepository, TimeProvider timeProvider) {
        this.petServiceRepository = petServiceRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public PetServiceSummaryResult execute (UpdatePetServiceCommand service) {
        validations(service);
        OffsetDateTime now = timeProvider.nowInUTC();
        PetService petService = loadExistingService(service.getId());
        petService.updateInfos(service.getName(), service.getPrice(), service.getDuration(), now);
        petServiceRepository.save(petService);

        PetServiceSummaryResult result = new PetServiceSummaryResult(petService.getId(),
                                                                petService.getName(),
                                                                petService.getPrice(),
                                                                petService.getDuration(),
                                                                petService.getCreatedAt(),
                                                                now);

        return (result);
    }

    private void validations(UpdatePetServiceCommand service) {

        int scheduleStep = 15;
        if (service.getId() == null || service.getId() <= 0)
            throw new DomainValidationException("Id do serviço é obrigatório");
        if (service.getName() != null && service.getName().isBlank())
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        if (service.getDuration() != null && (service.getDuration() < 30 || service.getDuration() > 180))
            throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
        if (service.getDuration() != null && service.getDuration() % scheduleStep != 0)
            throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
    } 

    private PetService loadExistingService(Long id) {
    
        Optional<PetService> findService = petServiceRepository.findById(id);
        if (findService.isEmpty()) 
            throw new ServiceNotFoundException("Serviço não encontrado");
        return (findService.get());
    }
    
}
