package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;

public class AddTimeOffRequest {
    private Long professionalId;
    @NotNull(message = "O motivo é obrigatório")
    private String reason;
    @NotNull(message = "O inicio da folga é obrigatório")
    private OffsetDateTime startAt;
    @NotNull(message = "O término da folga é obrigatório")
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

    public void setProfessionalId(Long id) {
        this.professionalId = id;
    }
}
