package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class Appointment {
    private Long id;
    private Long petId;
    private Long tutorId;
    private Long professionalId;
    private Long serviceId;
    private OffsetDateTime startAt;
    private int serviceDurationMinutes;
    private AppointmentStatus status;
    private String observations;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public Appointment () {
    }

    public Appointment (Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(petId, tutorId, professionalId, serviceId, startAt, serviceDurationMinutes, status, observations);
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.serviceDurationMinutes = serviceDurationMinutes;
        this.status = status;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;   
    }

    private Appointment (Long id, Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(petId, tutorId, professionalId, serviceId, startAt, serviceDurationMinutes, status, observations);
        this.id = id;
        this.petId = petId;
        this.tutorId = tutorId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.startAt = startAt;
        this.serviceDurationMinutes = serviceDurationMinutes;
        this.status = status;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;   
    }

    private void    basicValidations(Long petId, Long tutorId, Long professionalId, Long serviceId, OffsetDateTime startAt, int serviceDurationMinutes, AppointmentStatus status, String observations) {

        int scheduleStep = 15;

        if (petId == null || petId <= 0)
            throw new DomainValidationException("Necessário vincular um pet");
        if (tutorId == null || tutorId <= 0)
            throw new DomainValidationException("Necessário vincular um tutor");
        if (professionalId == null || professionalId <= 0)
            throw new DomainValidationException("Necessário vincular um profissional");
        if (serviceId == null || serviceId <= 0)
            throw new DomainValidationException("Necessário vincular um serviço");
        if (startAt == null)
            throw new DomainValidationException("Necessário horario do agendamento");
        if (serviceDurationMinutes < 30 || serviceDurationMinutes % scheduleStep != 0)
            throw new DomainValidationException("Necessário tempo de duração correto do serviço");
        if (status == null)
            throw new DomainValidationException("Necessário status do agendamento");
        if (startAt.getMinute() % scheduleStep != 0)
            throw new DomainValidationException("Horário de marcação deve ter minutos 00, 15, 30 ou 45");
    }

    public void rescheduleTo(OffsetDateTime newStartAt, OffsetDateTime nowUtc) {
        if (newStartAt == null || newStartAt.isEqual(this.startAt) || newStartAt.isBefore(nowUtc))
            throw new DomainValidationException("Novo horário inválido");
        if ((this.status == AppointmentStatus.CANCELLED || this.status == AppointmentStatus.COMPLETED))
            throw new DomainValidationException("Consulta inválida, favor agendar outra");
        this.startAt = newStartAt;
        this.updatedAt = nowUtc;
    }

    public void cancelSchedule(OffsetDateTime nowUtc) {
        if (this.status == AppointmentStatus.CANCELLED || this.status == AppointmentStatus.COMPLETED)
            throw new DomainValidationException("Consulta já encerrada");
        this.status = AppointmentStatus.CANCELLED;
        this.updatedAt = nowUtc;
    }

    public Appointment withPersistenceId (Long id) {
        if (id == null || id < 0)
            throw new DomainValidationException("Id inválido");
        return new Appointment(id, this.petId, this.tutorId, this.professionalId, this.serviceId, this.startAt, this.serviceDurationMinutes, this.status, this.observations, this.createdAt, this.updatedAt);
    }

    public Long getId() {
        return id;
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

    public int getServiceDuration() {
        return serviceDurationMinutes;
    }

    public OffsetDateTime getEndAt() {
        return this.startAt.plusMinutes(this.serviceDurationMinutes);
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getObservations() {
        return observations;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
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

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

}
