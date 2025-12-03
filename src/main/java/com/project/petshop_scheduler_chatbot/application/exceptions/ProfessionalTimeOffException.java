package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class ProfessionalTimeOffException extends BusinessException {
    public ProfessionalTimeOffException (String message) {
        super(message);
    }

    public ProfessionalTimeOffException (String message, Throwable cause) {
        super(message, cause);
    }
}
