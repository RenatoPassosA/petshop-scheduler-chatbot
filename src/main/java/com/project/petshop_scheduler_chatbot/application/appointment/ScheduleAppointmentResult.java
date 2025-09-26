package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class ScheduleAppointmentResult {
    final private Long appointmentId;
    final private Long serviceId;
    final private Long professionalId;
    final private String serviceName;
    final private OffsetDateTime startAt;
    final private OffsetDateTime endAt;
    final private AppointmentStatus status;

    public ScheduleAppointmentResult(Long appointmentId, Long serviceId, Long professionalId, String serviceName, OffsetDateTime startAt, OffsetDateTime endAt, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.professionalId = professionalId;
        this.serviceName = serviceName;
        this.startAt = startAt;
        this.endAt = endAt; 
        this.status = status;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    
}
