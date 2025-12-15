package com.project.petshop_scheduler_chatbot.application.petservices.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceResult;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultRegisterPetServiceUseCase implements PetServiceUseCase {
    final private PetServiceRepository petServiceRepository;
    final private TimeProvider timeProvider;
    
    public DefaultRegisterPetServiceUseCase (PetServiceRepository petServiceRepository, TimeProvider timeProvider) {
        this.petServiceRepository = petServiceRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public AddPetServiceResult register (AddPetServiceCommand service) {
        registerValidations(service);
        if (!petServiceRepository.findByName(service.getName()).isEmpty()) {
            throw new InvalidAppointmentStateException("Serviço já cadastrado");
        }
        PetService petService = new PetService(service.getName(),
                                            service.getPrice(),
                                            service.getDuration(),
                                            service.getCanDo(),
                                            timeProvider.nowInUTC(),
                                            timeProvider.nowInUTC());
        petService = petServiceRepository.save(petService);

        AddPetServiceResult result = new AddPetServiceResult(petService.getId(),
                                                                petService.getName(),
                                                                petService.getPrice(),
                                                                petService.getDuration()
                                                            );

        return (result);
    }

    private void registerValidations(AddPetServiceCommand service) {

        int scheduleStep = 15;

        if (service.getName() == null || service.getName().isBlank())
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        if (service.getDuration() < 30 || service.getDuration() > 180)
            throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
        if (service.getDuration() % scheduleStep != 0)
            throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
        if (service.getCanDo() == null)
            throw new DomainValidationException("Informar profissional capacitado");
    }

    @Override
    @Transactional
    public void update (Long id, UpdatePetServiceCommand service) {
        PetService petService = petServiceRepository.findById(id)
            .orElseThrow(() -> new PetServiceNotFoundException("Serviço não encontrado"));
        
        if (service.getName() != null) petService.setName(service.getName());
        if (service.getPrice() != null) petService.setPrice(service.getPrice());
        if (service.getDuration() != null) petService.setDuration(service.getDuration());
        petServiceRepository.save(petService);
    }

    @Override
    @Transactional(readOnly = true)
    public PetService getPetService (Long id) {
        return petServiceRepository.findById(id)
                            .orElseThrow(() -> new PetServiceNotFoundException("Serviço não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetService> getAll() {
        return petServiceRepository.getAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!petServiceRepository.existsById(id))
            throw new PetServiceNotFoundException("Serviço não encontrado");
        petServiceRepository.deleteById(id);
    }
}
