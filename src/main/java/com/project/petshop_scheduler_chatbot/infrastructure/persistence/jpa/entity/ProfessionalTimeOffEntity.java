package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professional_time_off")
public class ProfessionalTimeOffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long professionalId;
    @Column(nullable = false)
    private String reason;
    @Column(nullable = false)
    private OffsetDateTime startAt;
    @Column(nullable = false)
    private OffsetDateTime endAt;

    public ProfessionalTimeOffEntity () {
    }

    public ProfessionalTimeOffEntity (Long id, Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.id = id;
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProfessionalId() {
        return professionalId;
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
}
