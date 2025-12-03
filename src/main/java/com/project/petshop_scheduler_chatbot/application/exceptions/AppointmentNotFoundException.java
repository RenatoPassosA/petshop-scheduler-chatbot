package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class AppointmentNotFoundException extends BusinessException {
    public AppointmentNotFoundException (String message) {
        super(message);
    }

    public AppointmentNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
