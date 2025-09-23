package com.project.petshop_scheduler_chatbot.application.appointment;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class ConfirmAppointmentResult {
    final private Long appointmentId;
    final private AppointmentStatus status;

    public ConfirmAppointmentResult(Long appointmentId, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.status = status;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
}