package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.LocalDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class ScheduleAppointmentResult {
    final private Long appointmentId;
    final private Long serviceId;
    final private Long professionalId;
    final private LocalDateTime startAt;
    final private LocalDateTime endAt;
    final private AppointmentStatus status;

    public ScheduleAppointmentResult(Long appointmentId, Long serviceId, Long professionalId, LocalDateTime startAt, LocalDateTime endAt, AppointmentStatus status) {
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

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    
}
