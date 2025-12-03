package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class PetOverlapException extends BusinessException {
    public PetOverlapException (String message) {
        super(message);
    }

    public PetOverlapException (String message, Throwable cause) {
        super(message, cause);
    }
}
