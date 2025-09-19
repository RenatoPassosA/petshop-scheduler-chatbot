package com.project.petshop_scheduler_chatbot.core.domain.schedule;

import java.time.LocalDateTime;

public class ProfessionalTimeOff {
    private Long professionalId;
    private String reason;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public ProfessionalTimeOff () {
    }

    public ProfessionalTimeOff (Long professionalId, String reason, LocalDateTime startAt, LocalDateTime endAt) {
        basicValidations(professionalId, startAt, endAt);
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void    basicValidations(Long professionalId, LocalDateTime startAt, LocalDateTime endAt) {
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

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }
}

