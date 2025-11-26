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

import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.AddPetServiceRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.AddPetServiceResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.GetPetServiceResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.UpdatePetServiceRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.PetServiceWebMapper;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.PetWebMapper;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceResult;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/petservice")
public class PetServiceController {
    private final PetServiceUseCase petServiceUseCase;

    public PetServiceController (PetServiceUseCase petServiceUseCase) {
        this.petServiceUseCase = petServiceUseCase;
    }

    @PostMapping
    public ResponseEntity<AddPetServiceResponse> addPetService (@RequestBody @Valid AddPetServiceRequest request) {
        AddPetServiceCommand command = PetServiceWebMapper.toCommand(request);
        AddPetServiceResult result = petServiceUseCase.register(command);
        AddPetServiceResponse response = PetServiceWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePet(@PathVariable("id") @Positive Long id, @RequestBody @Valid UpdatePetServiceRequest request) {
        UpdatePetServiceCommand command = PetServiceWebMapper.toCommand(request);
        petServiceUseCase.update(id, command);
        return ResponseEntity.noContent().build();
    }

   @GetMapping("/{id}")
    public ResponseEntity<GetPetServiceResponse> getPet(@PathVariable("id") @Positive Long id) {
        PetService petService = petServiceUseCase.getPetService(id);
        GetPetServiceResponse response = PetWebMapper.toResponse(petService);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetPetServiceResponse>> getAll() {
        List<PetService> petService = petServiceUseCase.getAll();
        List<GetPetServiceResponse> response = new ArrayList<>();
        for (PetService petServices : petService) {
            GetPetServiceResponse petServiceResponse = new GetPetServiceResponse(petServices.getName(),
                                                                                petServices.getPrice(),
                                                                                petServices.getDuration());
            response.add(petServiceResponse);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable("id") @Positive Long id) {
        petServiceUseCase.delete(id);
        return ResponseEntity.noContent().build(); 
    }  
}
