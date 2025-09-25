package com.project.petshop_scheduler_chatbot.adapters.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class RescheduleAppointmentRequest {
    @NotNull(message = "A nova data é obrigatória")
    @FutureOrPresent(message = "A nova data deve ser futura")
    private LocalDateTime newStartAt;

    public RescheduleAppointmentRequest () {
    }

    public RescheduleAppointmentRequest (LocalDateTime newStartAt) {
        this.newStartAt = newStartAt;
    }

    public LocalDateTime getNewStartAt() {
        return newStartAt;
    }

    public void setNewStartAt(LocalDateTime newStartAt) {
        this.newStartAt = newStartAt;
    }
}
