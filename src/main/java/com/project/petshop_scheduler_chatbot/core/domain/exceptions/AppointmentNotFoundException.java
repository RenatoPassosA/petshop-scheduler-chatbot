package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class AppointmentNotFoundException extends BusinessException {
    public AppointmentNotFoundException (String message) {
        super(message);
    }

    public AppointmentNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
