package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(nullable = false)
    private String status;
    @Column
    private String observations;
    @Column(nullable = false)
    private OffsetDateTime createdAt;
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public AppointmentEntity() {
    }
    
    public AppointmentEntity (Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.endAt = startAt.plusMinutes(serviceDurationMinutes);
        this.serviceDurationMinutes = serviceDurationMinutes;
        this.status = status.name();
        this.observations = observations;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();   
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

    public String getStatus() {
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

    public void setId(Long id) {
        this.id = id;
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

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public void setServiceDurationMinutes(Integer serviceDurationMinutes) {
        this.serviceDurationMinutes = serviceDurationMinutes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
