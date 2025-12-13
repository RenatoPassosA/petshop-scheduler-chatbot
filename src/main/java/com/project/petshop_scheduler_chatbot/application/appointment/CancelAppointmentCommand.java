package com.project.petshop_scheduler_chatbot.application.appointment;

public class CancelAppointmentCommand {
    private Long appointmentId;

    public CancelAppointmentCommand() {
    }

    public CancelAppointmentCommand(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }
}
