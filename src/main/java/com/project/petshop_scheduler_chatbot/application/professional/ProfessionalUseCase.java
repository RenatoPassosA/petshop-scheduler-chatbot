package com.project.petshop_scheduler_chatbot.application.professional;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;

public interface ProfessionalUseCase {
    RegisterProfessionalResult execute (RegisterProfessionalCommand command);
    Professional getProfessional(Long id);
    void update(Long id, UpdateProfessionalCommand command);
    void delete(Long id);
}

