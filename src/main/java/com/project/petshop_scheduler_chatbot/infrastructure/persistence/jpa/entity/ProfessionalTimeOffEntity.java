package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "professional_time_off")
public class ProfessionalTimeOffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private ProfessionalEntity professional;
    @Column(nullable = true)
    private String reason;
    @Column(nullable = false)
    private OffsetDateTime startAt;
    @Column(nullable = false)
    private OffsetDateTime endAt;
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public ProfessionalTimeOffEntity () {
    }

    public ProfessionalTimeOffEntity (String reason, OffsetDateTime startAt, OffsetDateTime endAt, OffsetDateTime createdAt) {
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public ProfessionalEntity getProfessional() {
        return professional;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }   

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setProfessional(ProfessionalEntity professional) {
        this.professional = professional;
    }
}
