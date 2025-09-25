package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class TutorNotFoundException extends BusinessException {
public TutorNotFoundException (String message) {
        super(message);
    }

    public TutorNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}

