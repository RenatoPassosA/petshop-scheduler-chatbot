package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.LocalDateTime;

public class RescheduleAppointmentCommand {
    private Long appointmentId;
    private LocalDateTime newStartAt;

    public RescheduleAppointmentCommand() {
    }

    public RescheduleAppointmentCommand(Long appointmentId, LocalDateTime newStartAt) {
        this.appointmentId = appointmentId;
        this.newStartAt = newStartAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getNewStartAt() {
        return newStartAt;
    }
}