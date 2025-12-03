package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class TutorNotFoundException extends BusinessException {
public TutorNotFoundException (String message) {
        super(message);
    }

    public TutorNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}

