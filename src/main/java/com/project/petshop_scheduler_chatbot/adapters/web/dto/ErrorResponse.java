package com.project.petshop_scheduler_chatbot.adapters.web.dto;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private String code;
    private String message;
    private Integer status;
    private OffsetDateTime timestamp;
    private String path;
 
    public ErrorResponse(String code, String message, Integer status, OffsetDateTime timestamp, String path) {
        
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
