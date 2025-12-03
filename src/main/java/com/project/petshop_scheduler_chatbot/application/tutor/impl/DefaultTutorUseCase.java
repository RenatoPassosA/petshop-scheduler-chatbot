package com.project.petshop_scheduler_chatbot.application.tutor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.exceptions.DuplicatedPhoneNumberException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.application.tutor.UpdateTutorCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultTutorUseCase implements TutorUseCase{
    
    private final TutorRepository tutorRepository;
    private final TimeProvider timeProvider;

    public DefaultTutorUseCase (TutorRepository tutorRepository, TimeProvider timeProvider) {
        this.tutorRepository = tutorRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public AddTutorResult execute(AddTutorCommand tutorCommand) {
        validations(tutorCommand);
        Tutor tutor = new Tutor(tutorCommand.getName(),
                            tutorCommand.getPhoneNumber(),
                            tutorCommand.getAddress(),
                            this.timeProvider.nowInUTC(),
                            this.timeProvider.nowInUTC()
                            );
        
        if (tutorRepository.existsByPhone(tutor.getPhoneNumber()))
            throw new DuplicatedPhoneNumberException("Numero de celular já consta na base de dados");
        tutor = tutorRepository.save(tutor);
        AddTutorResult tutorResult = new AddTutorResult(tutor.getId(),
                                                    tutor.getName(),
                                                    tutor.getPhoneNumber().value(),
                                                    tutor.getAddress());
        return (tutorResult);
    }

    private void validations(AddTutorCommand tutorCommand) {
        if (tutorCommand.getName() == null || tutorCommand.getName().trim().isBlank())
            throw new DomainValidationException("Nome do Tutor é obrigatório");
        if (tutorCommand.getPhoneNumber() == null)
            throw new DomainValidationException("Telefone do Tutor é obrigatório");
        if (tutorCommand.getAddress() == null || tutorCommand.getAddress().trim().isBlank())
            throw new DomainValidationException("Endereço do Tutor é obrigatório");
    }

    @Override
    @Transactional
    public Tutor getTutor(Long id) {
        return tutorRepository.findById(id)
            .orElseThrow(() -> new TutorNotFoundException("Tutor não encontrado"));
    }


    @Override
    @Transactional
    public List<Tutor> getAll() {
        return tutorRepository.getAll();
    }
    
    @Override
    @Transactional
    public void update(Long id, UpdateTutorCommand command) {
        if (!tutorRepository.existsById(id))
            throw new TutorNotFoundException("Tutor não encontrado");
        Tutor tutor = tutorRepository.findById(id)
        .orElseThrow(() -> new TutorNotFoundException("Tutor não encontrado"));

        if (command.getName() != null) tutor.setName(command.getName());
        if (command.getPhoneNumber() != null) tutor.setPhone(command.getPhoneNumber());
        if (command.getAddress() != null) tutor.setAddress(command.getAddress());
        tutorRepository.save(tutor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tutorRepository.existsById(id))
            throw new TutorNotFoundException("Tutor não encontrado");
        tutorRepository.deleteById(id);
    }
}



