package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class DuplicatedPhoneNumberException extends BusinessException {
    public DuplicatedPhoneNumberException (String message) {
        super(message);
    }

    public DuplicatedPhoneNumberException (String message, Throwable cause) {
        super(message, cause);
    }
}