package com.project.petshop_scheduler_chatbot.application.appointment;

import java.time.LocalDateTime;

public class ScheduleAppointmentCommand {
    private Long petId;
    private Long tutorId;
    private Long professionalId;
    private Long serviceId;
    private LocalDateTime startAt;
    private String observation;
    
    public ScheduleAppointmentCommand() {
    }

    public ScheduleAppointmentCommand(Long petId, Long tutorId, Long professionalId, Long serviceId, LocalDateTime startAt, String observation) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.observation = observation;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public String getObservation() {
        return observation;
    }
}