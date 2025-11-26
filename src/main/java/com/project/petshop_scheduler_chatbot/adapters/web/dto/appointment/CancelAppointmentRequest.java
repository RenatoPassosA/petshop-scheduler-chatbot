package com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment;

public class CancelAppointmentRequest {
    private String reason;

    public CancelAppointmentRequest () {
    }

    public CancelAppointmentRequest (String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
