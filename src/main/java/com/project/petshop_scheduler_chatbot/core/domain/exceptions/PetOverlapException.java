package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class PetOverlapException extends BusinessException {
    public PetOverlapException (String message) {
        super(message);
    }

    public PetOverlapException (String message, Throwable cause) {
        super(message, cause);
    }
}
