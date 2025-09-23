package com.project.petshop_scheduler_chatbot.application.appointment;

public class CancelAppointmentCommand {
    private Long appointmentId;
    private String reason;

    public CancelAppointmentCommand() {
    }

    public CancelAppointmentCommand(Long appointmentId, String reason) {
        this.appointmentId = appointmentId;
        this.reason = reason;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public String getReason() {
        return reason;
    }
}
