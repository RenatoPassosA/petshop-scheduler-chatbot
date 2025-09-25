package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class AppointmentOverlapException extends BusinessException {
    public AppointmentOverlapException (String message) {
        super(message);
    }

    public AppointmentOverlapException (String message, Throwable cause) {
        super(message, cause);
    }
}
