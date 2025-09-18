package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.LocalDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class Appointment {
    private Long id;
    private Long petId;
    private Long tutorId;
    private Long professionalId;
    private Long serviceId;
    private int startAt;
    private int endAt;
    private AppointmentStatus status;
    private String observations;
    private LocalDateTime	createdAt;
    private LocalDateTime	updatedAt;

    public Appointment () {
    }

    public Appointment (Long petId, Long tutorId, Long professionalId, long serviceId, int startAt, int endAt, AppointmentStatus status, String observations) {
        basicValidations(petId, tutorId, professionalId, serviceId, startAt, endAt, status, observations);
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();   
    }

    private void    basicValidations(Long petId, Long tutorId, Long professionalId, Long serviceId, int startAt, int endAt, AppointmentStatus status, String observations) {
        if (petId == null)
            throw new IllegalArgumentException("Necessário vincular um pet");
        if (tutorId == null)
            throw new IllegalArgumentException("Necessário vincular um tutor");
        if (professionalId == null)
            throw new IllegalArgumentException("Necessário vincular um profissional");
        if (serviceId == null)
            throw new IllegalArgumentException("Necessário vincular um serviço");
        if (status == null)
            throw new IllegalArgumentException("Necessário status do agendamento");
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

    public int getStartAt() {
        return startAt;
    }

    public int getEndAt() {
        return endAt;
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

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(int endAt) {
        this.endAt = endAt;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}
