package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class ServiceNotFoundException extends BusinessException {
    public ServiceNotFoundException (String message) {
        super(message);
    }

    public ServiceNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
