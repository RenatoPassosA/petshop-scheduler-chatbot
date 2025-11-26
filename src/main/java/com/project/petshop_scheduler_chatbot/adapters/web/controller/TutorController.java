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

import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.AddTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.AddtutorRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.GetTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.UpdateTutorRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.TutorWebMapper;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.application.tutor.UpdateTutorCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/tutor")
public class TutorController {
    private final TutorUseCase tutorUseCase;

    public TutorController(TutorUseCase tutorUseCase) {
        this.tutorUseCase = tutorUseCase;
    }

    @PostMapping
    public ResponseEntity<AddTutorResponse> addTutor (@RequestBody @Valid AddtutorRequest request) {
        AddTutorCommand command = TutorWebMapper.toCommand(request);
        AddTutorResult result = tutorUseCase.execute(command);
        AddTutorResponse response = TutorWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTutor(@PathVariable("id") @Positive Long id, @RequestBody @Valid UpdateTutorRequest request) {
        UpdateTutorCommand command = TutorWebMapper.toCommand(request);
        tutorUseCase.update(id, command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetTutorResponse> getTutor(@PathVariable("id") @Positive Long id) {
        Tutor tutor = tutorUseCase.getTutor(id);
        GetTutorResponse response = TutorWebMapper.toResponse(tutor);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetTutorResponse>> getAll() {
        List<Tutor> tutors = tutorUseCase.getAll();
        List<GetTutorResponse> response = new ArrayList<>();
        for (Tutor tutor : tutors) {
            GetTutorResponse tutorResponse = new GetTutorResponse(tutor.getName(),
                                                                tutor.getPhoneNumber(),
                                                                tutor.getAddress());
            response.add(tutorResponse);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutor(@PathVariable("id") @Positive Long id) {
        tutorUseCase.delete(id);
        return ResponseEntity.noContent().build(); 
    }
}
