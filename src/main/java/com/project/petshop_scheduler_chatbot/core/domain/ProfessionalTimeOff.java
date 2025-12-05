package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public class ProfessionalTimeOff {
    private Long id;
    private Long professionalId;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;

    public ProfessionalTimeOff(Long id, Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        if (reason == null)
            throw new DomainValidationException("Motivo é obrigatório");
        reason = reason.trim();
        basicValidations(professionalId, reason, startAt, endAt);
        this.id = id;
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void    basicValidations(Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        if (professionalId == null || professionalId <= 0)
            throw new DomainValidationException("Profissional inválido");
        if (reason.isBlank())
            throw new DomainValidationException("Motivo é obrigatório");
        if (startAt == null)
            throw new DomainValidationException("startAt é obrigatório");
        if (endAt == null)
            throw new DomainValidationException("endAt é obrigatório");
        if (startAt.isAfter(endAt) || startAt.isEqual(endAt))
            throw new DomainValidationException("startAt deve ser antes de endAt");
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
