package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.AppointmentEntity;

@Component
public class AppointmentMapper {
    public AppointmentEntity toJPA(Appointment appointment) {
        if (appointment == null)
            throw new DomainValidationException("Dados de entrada invalidos");
        AppointmentEntity entity = new AppointmentEntity();

        entity.setId(appointment.getId());
        entity.setPetId(appointment.getPetId());
        entity.setTutorId(appointment.getTutorId());
        entity.setProfessionalId(appointment.getProfessionalId());
        entity.setServiceId(appointment.getServiceId());
        entity.setStartAt(appointment.getStartAt());
        entity.setServiceDurationMinutes(appointment.getServiceDuration());
        entity.setStatus(appointment.getStatus());
        entity.setObservations(appointment.getObservations());
        entity.setCreatedAt(appointment.getCreatedAt());
        entity.setUpdatedAt(appointment.getUpdatedAt());

        return entity;
    }

    

    public Appointment toDomain(AppointmentEntity entity) {
        if (entity == null)
            throw new DomainValidationException("Dados de entrada invalidos");
        Appointment appointment = new Appointment(entity.getPetId(),
                                                entity.getTutorId(),
                                                entity.getProfessionalId(),
                                                entity.getServiceId(),
                                                entity.getStartAt(),
                                                entity.getServiceDurationMinutes(),
                                                entity.getStatus(),
                                                entity.getObservations(),
                                                entity.getCreatedAt(),
                                                entity.getUpdatedAt()
                                );
        appointment = appointment.withPersistenceId(entity.getId());
        return (appointment);
    }
}