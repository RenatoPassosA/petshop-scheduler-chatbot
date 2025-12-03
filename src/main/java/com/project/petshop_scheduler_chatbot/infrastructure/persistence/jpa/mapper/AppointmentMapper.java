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
        return new AppointmentEntity(appointment.getPetId(),
                                    appointment.getTutorId(),
                                    appointment.getProfessionalId(),
                                    appointment.getServiceId(),
                                    appointment.getStartAt(),
                                    appointment.getServiceDuration(),
                                    appointment.getStatus(),
                                    appointment.getObservations(),
                                    appointment.getCreatedAt(),
                                    appointment.getUpdatedAt()
                                    );

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