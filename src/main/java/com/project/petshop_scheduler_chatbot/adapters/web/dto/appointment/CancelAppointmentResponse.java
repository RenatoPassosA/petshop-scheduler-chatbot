package com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment;

public class CancelAppointmentResponse {
    final private Long appointmentId;
    final private String serviceName;
    final private String status;

    public CancelAppointmentResponse (Long appointmentId, String serviceName, String status) {
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

    public String getStatus() {
        return status;
    }
}