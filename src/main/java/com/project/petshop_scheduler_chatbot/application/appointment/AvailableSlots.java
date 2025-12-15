package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.OffsetDateTime;

public class AvailableSlots {
    private final OffsetDateTime    startAt;
    private final Long              professionalId;
    private final String            professionalName;

    public AvailableSlots (OffsetDateTime startAt, Long professionalId, String professionalName) {
        this.startAt = startAt;
        this.professionalId = professionalId;
        this.professionalName = professionalName;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public String getProfessionalName() {
        return professionalName;
    }

}
