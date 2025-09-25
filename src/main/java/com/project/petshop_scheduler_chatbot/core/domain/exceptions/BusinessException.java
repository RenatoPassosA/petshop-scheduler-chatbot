package com.project.petshop_scheduler_chatbot.core.domain.exceptions;

abstract public class BusinessException extends RuntimeException{

    public BusinessException (String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}