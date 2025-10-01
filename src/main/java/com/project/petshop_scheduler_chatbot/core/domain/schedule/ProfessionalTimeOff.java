package com.project.petshop_scheduler_chatbot.core.domain.schedule;

import java.time.OffsetDateTime;

public class ProfessionalTimeOff {
    private Long professionalId;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    public ProfessionalTimeOff () {
    }

    public ProfessionalTimeOff (Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        basicValidations(professionalId, startAt, endAt);
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void    basicValidations(Long professionalId, OffsetDateTime startAt, OffsetDateTime endAt) {
        if (professionalId == null)
            throw new IllegalArgumentException("Id do profissional é obrigatório");
        if (startAt == null)
            throw new IllegalArgumentException("Inicio do período do afastamento é obrigatório");
        if (endAt == null)
            throw new IllegalArgumentException("Término do período do afastamento é obrigatório");
        if (endAt.isBefore(startAt) || startAt.isEqual(endAt))
            throw new IllegalArgumentException("Período de afastamento inválid");
        
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

    public void setProfessionalId(Long professionalId) {
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

