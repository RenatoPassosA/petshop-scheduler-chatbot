package com.project.petshop_scheduler_chatbot.application.appointment;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class CancelAppointmentResult {
    final private Long appointmentId;
    final private String serviceName;
    final private AppointmentStatus status;

    public CancelAppointmentResult(Long appointmentId, String serviceName, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.serviceName = serviceName;
        this.status = status;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

     public String getServiceName() {
        return serviceName;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
}
