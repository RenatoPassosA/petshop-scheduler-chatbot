package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;

public class RescheduleAppointmentCommand {
    private Long appointmentId;
    private OffsetDateTime newStartAt;

    public RescheduleAppointmentCommand() {
    }

    public RescheduleAppointmentCommand(Long appointmentId, OffsetDateTime newStartAt) {
        this.appointmentId = appointmentId;
        this.newStartAt = newStartAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public OffsetDateTime getNewStartAt() {
        return newStartAt;
    }
}