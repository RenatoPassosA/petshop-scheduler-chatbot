package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class RescheduleAppointmentResult {
    final private Long appointmentId;
    final private Long serviceId;
    final private Long professionalId;
    final private OffsetDateTime startAt;
    final private OffsetDateTime endAt;
    final private AppointmentStatus status;

    public RescheduleAppointmentResult(Long appointmentId, Long serviceId, Long professionalId, OffsetDateTime startAt, OffsetDateTime endAt, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.professionalId = professionalId;
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

