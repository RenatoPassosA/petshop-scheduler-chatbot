package com.project.petshop_scheduler_chatbot.application.professional;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;

public interface ProfessionalUseCase {
    AddProfessionalResult execute (AddProfessionalCommand command);
    Professional getProfessional(Long id);
    List<Professional> getAll();
    void update(Long id, UpdateProfessionalCommand command);
    void delete(Long id);
}

