package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class ServiceNotFoundException extends BusinessException {
    public ServiceNotFoundException (String message) {
        super(message);
    }

    public ServiceNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
