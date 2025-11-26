package com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment;

import java.time.OffsetDateTime;

public class ScheduleAppointmentResponse {
    final private Long appointmentId;
    final private Long serviceId;
    final private String serviceName;
    final private Long professionalId;
    final private OffsetDateTime startAt;
    final private OffsetDateTime endAt;
    final private String status;

    public ScheduleAppointmentResponse (Long appointmentId, Long serviceId, String serviceName, Long professionalId, OffsetDateTime startAt,
        OffsetDateTime endAt, String status) {
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
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

    public String getServiceName() {
        return serviceName;
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

    public String getStatus() {
        return status;
    } 
}


