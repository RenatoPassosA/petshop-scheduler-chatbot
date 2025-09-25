package com.project.petshop_scheduler_chatbot.adapters.web.dto;

import java.time.LocalDateTime;

public class ScheduleAppointmentResponse {
    final private Long appointmentId;
    final private Long serviceId;
    final private String serviceName;
    final private Long professionalId;
    final private LocalDateTime startAt;
    final private LocalDateTime endAt;
    final private String status;

    public ScheduleAppointmentResponse (Long appointmentId, Long serviceId, String serviceName, Long professionalId, LocalDateTime startAt,
        LocalDateTime endAt, String status) {
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

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getStatus() {
        return status;
    } 
}


