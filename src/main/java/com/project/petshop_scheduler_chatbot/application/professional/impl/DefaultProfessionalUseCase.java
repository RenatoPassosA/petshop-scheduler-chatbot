package com.project.petshop_scheduler_chatbot.application.professional.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.professional.RegisterProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.RegisterProfessionalResult;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.ProfessionalUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;

@Service
public class DefaultProfessionalUseCase implements ProfessionalUseCase{

    private final ProfessionalRepository professionalRepository;
    private final TimeProvider timeProvider;

    public DefaultProfessionalUseCase (ProfessionalRepository professionalRepository, TimeProvider timeProvider) {
        this.professionalRepository = professionalRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public RegisterProfessionalResult execute(RegisterProfessionalCommand professionalCommand) {
        validations(professionalCommand);
        Professional professional = new Professional(professionalCommand.getName(),
                                                professionalCommand.getFunction(),
                                                this.timeProvider.nowInUTC(),
                                                this.timeProvider.nowInUTC()
                                                );
        
        
        professionalRepository.save(professional);
        RegisterProfessionalResult professionalResult = new RegisterProfessionalResult(professional.getName(),professional.getFunction());
        return (professionalResult);
    }

    private void validations(RegisterProfessionalCommand professional) {
        if (professional == null)
            throw new DomainValidationException("Comando inválido");
        if (professional.getName() == null || professional.getName().trim().isBlank())
            throw new IllegalArgumentException("Nome do Colaborador é obrigatório");
        if (professional.getFunction() == null)
            throw new IllegalArgumentException("Função do colaborador é obrigatória");
    }

    @Override
    @Transactional
    public Professional getProfessional(Long id) {
        Optional<Professional> professional = professionalRepository.findById(id);
        if (!professional.isPresent())
            throw new ProfessionalNotFoundException("Profissional não encontrado");
        return professional.get();
    }

    @Override
    @Transactional
    public void update(Long id, UpdateProfessionalCommand command) {
        if (!professionalRepository.existsById(id))
            throw new ProfessionalNotFoundException("Profissional não encontrado");
        Professional professional = professionalRepository.findById(id)
        .orElseThrow(() -> new ProfessionalNotFoundException("Profissional não encontrado"));

        professional.setName(command.getName());
        professional.setFunction(command.getFunction());
        professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!professionalRepository.existsById(id))
            throw new ProfessionalNotFoundException("Profissional não encontrado");
        professionalRepository.deleteById(id);
    }
}
