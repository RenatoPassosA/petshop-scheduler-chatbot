package com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class RescheduleAppointmentRequest {
    @NotNull(message = "A nova data é obrigatória")
    @FutureOrPresent(message = "A nova data deve ser futura")
    private OffsetDateTime newStartAt;

    public RescheduleAppointmentRequest () {
    }

    public RescheduleAppointmentRequest (OffsetDateTime newStartAt) {
        this.newStartAt = newStartAt;
    }

    public OffsetDateTime getNewStartAt() {
        return newStartAt;
    }

    public void setNewStartAt(OffsetDateTime newStartAt) {
        this.newStartAt = newStartAt;
    }
}
