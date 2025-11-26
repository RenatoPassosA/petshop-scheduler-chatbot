package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AddTimeOffRequest {
    @NotNull(message = "O colaborador é obrigatório")
    @Positive(message = "O ID deve ser positivo")
    private Long professionalId;
    @NotNull(message = "O motivo é obrigatório")
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    
    public AddTimeOffRequest(Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.professionalId = professionalId;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
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
