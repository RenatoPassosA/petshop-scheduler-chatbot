package com.project.petshop_scheduler_chatbot.application.professional;

import java.time.OffsetDateTime;

public class AddTimeOffCommand {
    private Long professionalId;
    private String reason;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    
    public AddTimeOffCommand(Long professionalId, String reason, OffsetDateTime startAt, OffsetDateTime endAt) {
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
