package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class InvalidAppointmentStateException extends BusinessException {
    public InvalidAppointmentStateException (String message) {
        super(message);
    }

    public InvalidAppointmentStateException (String message, Throwable cause) {
        super(message, cause);
    }
}
