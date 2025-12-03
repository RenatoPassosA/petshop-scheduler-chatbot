package com.project.petshop_scheduler_chatbot.adapters.web.controller;

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

import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddProfessionalRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddProfessionalResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddTimeOffRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddTimeOffResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.GetProfessionalResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.UpdateProfessionalRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.ProfessionalWebMapper;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.TimeOffWebMapper;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import com.project.petshop_scheduler_chatbot.application.professional.ProfessionalUseCase;

import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.application.professional.TimeOffUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;

@Validated
@RestController
@RequestMapping("/professional")
public class ProfessionalController {

    private final ProfessionalUseCase professionalUseCase;
    private final TimeOffUseCase timeOffUseCase;

    public ProfessionalController (TimeOffUseCase timeOffUseCase, ProfessionalUseCase professionalUseCase) {
        this.timeOffUseCase = timeOffUseCase;
        this.professionalUseCase = professionalUseCase;
    }

    @PostMapping
    public ResponseEntity<AddProfessionalResponse> addProfessional(@RequestBody @Valid AddProfessionalRequest request) {
        AddProfessionalCommand command = ProfessionalWebMapper.toCommand(request);
        AddProfessionalResult result = professionalUseCase.execute(command);
        AddProfessionalResponse response = ProfessionalWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/timeoff")
    public ResponseEntity<AddTimeOffResponse> addTimeOff(@PathVariable("id") @Positive Long id, @RequestBody @Valid AddTimeOffRequest request) {
        AddTimeOffCommand command = TimeOffWebMapper.toCommand(id, request);
        AddTimeOffResult result = timeOffUseCase.execute(command);
        AddTimeOffResponse response = TimeOffWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProfessionalResponse> getProfessional(@PathVariable("id") @Positive Long id) {
        Professional professional = professionalUseCase.getProfessional(id);
        GetProfessionalResponse response = ProfessionalWebMapper.toResponse(professional);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProfessional(@PathVariable("id") @Positive Long id, @RequestBody @Valid UpdateProfessionalRequest request) {
        UpdateProfessionalCommand command = ProfessionalWebMapper.toCommand(request);
        professionalUseCase.update(id, command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable("id") @Positive Long id) {
        professionalUseCase.delete(id);
        return ResponseEntity.noContent().build(); 
    }

    @DeleteMapping("/{professionalId}/timeoff/{timeOffId}")
    public ResponseEntity<Void> deleteTimeOff(@PathVariable("professionalId") @Positive Long professionalId, @PathVariable("timeOffId") @Positive Long timeOffId) {
        timeOffUseCase.delete(professionalId, timeOffId);
        return ResponseEntity.noContent().build();
    }
}