package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class PetNotFoundException extends BusinessException {
    public PetNotFoundException (String message) {
        super(message);
    }

    public PetNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
