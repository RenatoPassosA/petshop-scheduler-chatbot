package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class ProfessionalTimeOffException extends BusinessException {
    public ProfessionalTimeOffException (String message) {
        super(message);
    }

    public ProfessionalTimeOffException (String message, Throwable cause) {
        super(message, cause);
    }
}
