package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.AddPetToTutorRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.AddPetToTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.GetPetResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.UpdatePetRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.PetWebMapper;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorResult;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.application.pet.UpdatePetCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/pet")
public class PetController {
    private final PetUseCase petUseCase;
    private final TutorUseCase tutorUseCase;

    public PetController(PetUseCase petUseCase, TutorUseCase tutorUseCase) {
        this.petUseCase = petUseCase;
        this.tutorUseCase = tutorUseCase;
    }

    @PostMapping
    public ResponseEntity<AddPetToTutorResponse> addPetToTutor (@RequestBody @Valid AddPetToTutorRequest request) {
        String tutorName = tutorUseCase.getTutor(request.getTutorId()).getName();
        AddPetToTutorCommand command = PetWebMapper.toCommand(request);
        AddPetToTutorResult result = petUseCase.execute(command);
        AddPetToTutorResponse response = PetWebMapper.toResponse(result, tutorName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePet(@PathVariable("id") @Positive Long id, @RequestBody @Valid UpdatePetRequest request) {
        UpdatePetCommand command = PetWebMapper.toCommand(request);
        petUseCase.update(id, command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPetResponse> getPet(@PathVariable("id") @Positive Long id) {
        Pet pet = petUseCase.getPet(id);
        String tutorName = tutorUseCase.getTutor(pet.getTutorId()).getName();
        if (tutorUseCase.getTutor(pet.getTutorId()) == null)
                throw new TutorNotFoundException("Tutor não encontrado");
        GetPetResponse response = PetWebMapper.toResponse(pet, tutorName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetPetResponse>> getAll() {
        List<Pet> pets = petUseCase.getAll();
        List<GetPetResponse> response = new ArrayList<>();
        for (Pet pet : pets) {
            String tutorName = tutorUseCase.getTutor(pet.getTutorId()).getName();
            if (tutorUseCase.getTutor(pet.getTutorId()) == null)
                throw new TutorNotFoundException("Tutor não encontrado");
            GetPetResponse petResponse = new GetPetResponse(pet.getName(),
                                                        tutorName,
                                                        pet.getGender(),
                                                        pet.getSize(),
                                                        pet.getBreed(),
                                                        pet.getTutorId(),
                                                        pet.getObservations());
            response.add(petResponse);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable("id") @Positive Long id) {
        petUseCase.delete(id);
        return ResponseEntity.noContent().build(); 
    }  
}
