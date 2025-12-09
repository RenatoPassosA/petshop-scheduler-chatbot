package com.project.petshop_scheduler_chatbot.application.professional;

import java.time.OffsetDateTime;

public class AddTimeOffResult {
    private Long professionalId;
    private String professionalName;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    
    public AddTimeOffResult(Long professionalId, String professionalName, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
        this.professionalId = professionalId;
        this.professionalName = professionalName;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getProfessionalId() {
        return professionalId;
    }
    
    public String getProfessionalName() {
        return professionalName;
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
