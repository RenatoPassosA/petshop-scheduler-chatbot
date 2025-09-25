package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

public class WorkingHoursOutsideException extends BusinessException {
    public WorkingHoursOutsideException (String message) {
        super(message);
    }

    public WorkingHoursOutsideException (String message, Throwable cause) {
        super(message, cause);
    }
}
