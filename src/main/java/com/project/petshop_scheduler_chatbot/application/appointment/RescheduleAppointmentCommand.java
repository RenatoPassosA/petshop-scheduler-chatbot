package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.LocalDateTime;

public class RescheduleAppointmentCommand {
    private Long appointmentId;
    private LocalDateTime newStartAt;
    private String observation;

    public RescheduleAppointmentCommand() {
    }

    public RescheduleAppointmentCommand(Long appointmentId, LocalDateTime newStartAt, String observation) {
        this.appointmentId = appointmentId;
        this.newStartAt = newStartAt;
        this.observation = observation;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public LocalDateTime getNewStartAt() {
        return newStartAt;
    }

    public String getObservation() {
        return observation;
    }

}
