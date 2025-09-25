package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class DomainValidationException extends BusinessException {
    public DomainValidationException (String message) {
        super(message);
    }

    public DomainValidationException (String message, Throwable cause) {
        super(message, cause);
    }
}
