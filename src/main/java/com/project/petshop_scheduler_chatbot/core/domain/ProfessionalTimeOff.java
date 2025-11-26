package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

public class ProfessionalTimeOff {
    private Long id;
    private Long professionalId;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    public ProfessionalTimeOff(Long id, Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.id = id;
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getId() {
        return id;
    }
    
    public Long getprofessionalId() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setprofessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }
}
