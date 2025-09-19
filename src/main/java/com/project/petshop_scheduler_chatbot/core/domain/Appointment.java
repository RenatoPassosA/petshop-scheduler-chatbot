package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.LocalDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class Appointment {
    private Long id;
    private Long petId;
    private Long tutorId;
    private Long professionalId;
    private Long serviceId;
    private LocalDateTime startAt;
    private int serviceDurationMinutes;
    private AppointmentStatus status;
    private String observations;
    private LocalDateTime	createdAt;
    private LocalDateTime	updatedAt;

    public Appointment () {
    }

    public Appointment (Long petId, Long tutorId, Long professionalId, Long serviceId, LocalDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations) {
        basicValidations(petId, tutorId, professionalId, serviceId, startAt, serviceDurationMinutes, status, observations);
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.serviceDurationMinutes = serviceDurationMinutes;
        this.status = status;
        this.observations = observations;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();   
    }

    private void    basicValidations(Long petId, Long tutorId, Long professionalId, Long serviceId, LocalDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations) {

        int scheduleStep = 15;

        if (petId == null || petId <= 0)
            throw new IllegalArgumentException("Necessário vincular um pet");
        if (tutorId == null || tutorId <= 0)
            throw new IllegalArgumentException("Necessário vincular um tutor");
        if (professionalId == null || professionalId <= 0)
            throw new IllegalArgumentException("Necessário vincular um profissional");
        if (serviceId == null || serviceId <= 0)
            throw new IllegalArgumentException("Necessário vincular um serviço");
        if (startAt == null)
            throw new IllegalArgumentException("Necessário horario do agendamento");
        if (serviceDurationMinutes < 30 || serviceDurationMinutes % scheduleStep != 0)
            throw new IllegalArgumentException("Necessário tempo de duração do serviço");
        if (status == null)
            throw new IllegalArgumentException("Necessário status do agendamento");
        if (startAt.getMinute() % scheduleStep != 0)
            throw new IllegalArgumentException("Horário de marcação deve ter minutos 00, 15, 30 ou 45");
    }

    public Long getId() {
        return id;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return this.startAt.plusMinutes(this.serviceDurationMinutes);
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getObservations() {
        return observations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}
