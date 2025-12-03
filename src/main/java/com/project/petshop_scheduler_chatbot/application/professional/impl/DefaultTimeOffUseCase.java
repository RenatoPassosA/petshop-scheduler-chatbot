package com.project.petshop_scheduler_chatbot.application.professional.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.professional.TimeOffUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;


@Service
public class DefaultTimeOffUseCase implements TimeOffUseCase{
    
    private final ProfessionalTimeOffRepository professionalTimeOffRepository;
    private final BusinessHoursPolicy businessHoursPolicy;
    private final ProfessionalRepository professionalRepository;
    private final TimeProvider timeProvider;

    public DefaultTimeOffUseCase (ProfessionalTimeOffRepository professionalTimeOffRepository, BusinessHoursPolicy businessHoursPolicy, ProfessionalRepository professionalRepository, TimeProvider timeProvider) {
        this.professionalTimeOffRepository = professionalTimeOffRepository;
        this.businessHoursPolicy = businessHoursPolicy;
        this.professionalRepository = professionalRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public AddTimeOffResult execute (AddTimeOffCommand timeOff) {
        validations(timeOff);
        professionalTimeOffRepository.save(timeOff.getProfessionalId(),
                                        timeOff.getStartAt(),
                                        timeOff.getEndAt(),
                                        timeOff.getReason(),
                                        timeProvider.nowInUTC());

        final String name = getName(timeOff);
        return new AddTimeOffResult(timeOff.getProfessionalId(),
                                name,
                                timeOff.getReason(),
                                timeOff.getStartAt(),
                                timeOff.getEndAt());
    }

    private void validations(AddTimeOffCommand timeOffCommand) {
        if (timeOffCommand == null)
            throw new DomainValidationException("Comando inválido");
        if (timeOffCommand.getProfessionalId() == null || timeOffCommand.getProfessionalId() <= 0)
            throw new DomainValidationException("Id do profissional inválido");
        if (timeOffCommand.getStartAt() == null || timeOffCommand.getEndAt() == null)
            throw new DomainValidationException("Horários de folga inválidos");  
        if (timeOffCommand.getEndAt().isBefore(timeOffCommand.getStartAt()))
            throw new DomainValidationException("Horários de folga inválidos"); 
        if (!professionalRepository.existsById(timeOffCommand.getProfessionalId()))
            throw new ProfessionalNotFoundException("Profissional nao cadastrado");
        if (!businessHoursPolicy.fits(timeOffCommand.getStartAt(), timeOffCommand.getEndAt()))
            throw new WorkingHoursOutsideException("Horário fora do expediente");
        if (professionalTimeOffRepository.existsOverlap(timeOffCommand.getProfessionalId(), timeOffCommand.getStartAt(), timeOffCommand.getEndAt()))
            throw new ProfessionalTimeOffException("Folga já cadastrada");
    }

    private String getName(AddTimeOffCommand timeOff) {
        Long profId = timeOff.getProfessionalId();
        String professionalName = professionalRepository.findById(profId)
                                                        .map(Professional::getName)
                                                        .orElseThrow(() -> new ProfessionalNotFoundException("Profissional não encontrado"));
        return (professionalName);
    }

    @Override
    @Transactional
    public void delete(Long professionalId, Long timeOffId) {
        if (!professionalRepository.existsById(professionalId))
            throw new ProfessionalNotFoundException("Profissional não encontrado");
        if (!professionalTimeOffRepository.existsById(timeOffId))
            throw new ProfessionalTimeOffException("TimeOff não encontrado");
        professionalTimeOffRepository.deleteById(timeOffId);
    }
}
