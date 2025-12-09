package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class PetServiceNotFoundException extends BusinessException {
    public PetServiceNotFoundException (String message) {
        super(message);
    }

    public PetServiceNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
