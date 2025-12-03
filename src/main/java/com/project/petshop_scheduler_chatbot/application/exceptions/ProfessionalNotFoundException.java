package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class ProfessionalNotFoundException extends BusinessException {
    public ProfessionalNotFoundException (String message) {
        super(message);
    }

    public ProfessionalNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
