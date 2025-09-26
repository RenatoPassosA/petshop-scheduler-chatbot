package com.project.petshop_scheduler_chatbot.adapters.web.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ScheduleAppointmentRequest {
    @NotNull(message = "O pet é obrigatório")
    @Positive(message = "O ID deve ser positivo")
    private Long petId;
    @NotNull(message = "O tutor é obrigatório")
    @Positive(message = "O ID deve ser positivo")
    private Long tutorId;
    @NotNull(message = "O profissional é obrigatório")
    @Positive(message = "O ID deve ser positivo")
    private Long professionalId;
    @NotNull(message = "O serviço é obrigatório")
    @Positive(message = "O ID deve ser positivo")
    private Long serviceId;
    @NotNull(message = "O horário é obrigatório")
    @FutureOrPresent(message = "A data deve ser futura")
    private OffsetDateTime startAt;
    private String observation;

    public ScheduleAppointmentRequest () {
    }

    public ScheduleAppointmentRequest (Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, String observation) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.observation = observation;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public void setObservation(String observation) {
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

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public String getObservation() {
        return observation;
    }

    
    
}

