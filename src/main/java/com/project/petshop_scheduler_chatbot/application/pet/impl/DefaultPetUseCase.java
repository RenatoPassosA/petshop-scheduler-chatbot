package com.project.petshop_scheduler_chatbot.application.pet.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorResult;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.application.pet.UpdatePetCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.PetNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultPetUseCase implements PetUseCase{
    
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final TimeProvider timeProvider;

    public DefaultPetUseCase (PetRepository petRepository, TutorRepository tutorRepository, TimeProvider timeProvider) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public AddPetToTutorResult execute(AddPetToTutorCommand petCommand) {
        validations(petCommand);
        Pet pet = new Pet(petCommand.getName(),
                        petCommand.getGender(),
                        petCommand.getSize(),
                        petCommand.getBreed(),
                        petCommand.getTutorId(),
                        petCommand.getObservation(),
                        this.timeProvider.nowInUTC(),
                        this.timeProvider.nowInUTC()
                        );
        
        pet = petRepository.save(pet);
        AddPetToTutorResult petResult = new AddPetToTutorResult(pet.getId(), pet.getTutorId(), pet.getName(), pet.getObservations());
        return (petResult);
    }

    private void validations(AddPetToTutorCommand petCommand) {
        if (petCommand == null)
            throw new IllegalArgumentException("Dados inválidos");
        if (petCommand.getTutorId() == null || petCommand.getTutorId() <= 0)
            throw new IllegalArgumentException("ID do Tutor é obrigatório");
        if (petCommand.getName() == null || petCommand.getName().trim().isBlank())
            throw new IllegalArgumentException("Nome do Pet é obrigatório");
        if (!tutorRepository.existsById(petCommand.getTutorId()))
            throw new IllegalArgumentException("Tutor não existente no banco de dados");
    }

    @Override
    @Transactional
    public Pet getPet(Long id) {
        return petRepository.findById(id)
                            .orElseThrow(() -> new PetNotFoundException("Pet não encontrado"));
    }


    @Override
    @Transactional
    public List<Pet> getAll() {
        return petRepository.getAll();
    }

    @Override
    @Transactional
    public void update(Long petId, UpdatePetCommand command) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new PetNotFoundException("Pet não encontrado"));
        pet.setObservations(command.getObservation());
        petRepository.save(pet);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!petRepository.existsById(id))
            throw new PetNotFoundException("Pet não encontrado");
        petRepository.deleteById(id);
    }
}