package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class ProfessionalNotFoundException extends BusinessException {
    public ProfessionalNotFoundException (String message) {
        super(message);
    }

    public ProfessionalNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
