package com.project.petshop_scheduler_chatbot.application.exceptions;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.BusinessException;

public class AppointmentOverlapException extends BusinessException {
    public AppointmentOverlapException (String message) {
        super(message);
    }

    public AppointmentOverlapException (String message, Throwable cause) {
        super(message, cause);
    }
}
