package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long petId;
    @Column(nullable = false)
    private Long tutorId;
    @Column(nullable = false)
    private Long professionalId;
    @Column(nullable = false)
    private Long serviceId;
    @Column(nullable = false)
    private OffsetDateTime startAt;
    @Column(nullable = false)
    private OffsetDateTime endAt;
    @Column(nullable = false)
    private Integer serviceDurationMinutes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
    @Column
    private String observations;
    @Column(nullable = false)
    private OffsetDateTime createdAt;
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public AppointmentEntity() {
    }
    
    public AppointmentEntity (Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, OffsetDateTime endAt, int serviceDurationMinutes, AppointmentStatus status, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.serviceDurationMinutes = serviceDurationMinutes;
        this.status = status;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;   
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

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public Integer getServiceDurationMinutes() {
        return serviceDurationMinutes;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getObservations() {
        return observations;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
