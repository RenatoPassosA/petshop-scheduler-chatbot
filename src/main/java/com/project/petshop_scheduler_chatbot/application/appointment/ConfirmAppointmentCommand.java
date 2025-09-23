package com.project.petshop_scheduler_chatbot.application.appointment;

public class ConfirmAppointmentCommand {
    private Long appointmentId;

    public ConfirmAppointmentCommand() {
    }

    public ConfirmAppointmentCommand(Long appointmentId, String reason) {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }
}