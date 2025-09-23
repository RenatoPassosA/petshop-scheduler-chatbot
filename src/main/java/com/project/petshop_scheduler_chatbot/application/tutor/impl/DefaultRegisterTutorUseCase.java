package com.project.petshop_scheduler_chatbot.application.tutor.impl;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.tutor.RegisterTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.RegisterTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.RegisterTutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultRegisterTutorUseCase implements RegisterTutorUseCase{
    
    private final TutorRepository tutorRepository;

    public DefaultRegisterTutorUseCase (TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public RegisterTutorResult execute(RegisterTutorCommand tutorCommand) {
        validations(tutorCommand);
        Tutor tutor = new Tutor(
            tutorCommand.getName(),
            tutorCommand.getPhoneNumber(),
            tutorCommand.getAddress(),
            null
            );
        
        if (tutorRepository.existsByPhone(tutor.getPhoneNumber()))
            throw new IllegalArgumentException("Numero de celular já consta na base de dados");
        tutorRepository.save(tutor);
        RegisterTutorResult tutorResult = new RegisterTutorResult(tutor.getId(), tutor.getName(), tutor.getPhoneNumber().value());
        return (tutorResult);
    }

    private void validations(RegisterTutorCommand tutorCommand) {
        if (tutorCommand.getName() == null || tutorCommand.getName().trim().isBlank())
            throw new IllegalArgumentException("Nome do Tutor é obrigatório");
        if (tutorCommand.getPhoneNumber() == null)
            throw new IllegalArgumentException("Telefone do Tutor é obrigatório");
        if (tutorCommand.getAddress() == null || tutorCommand.getAddress().trim().isBlank())
            throw new IllegalArgumentException("Endereço do Tutor é obrigatório");

    }
}
