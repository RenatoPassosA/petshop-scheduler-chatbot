package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.AppointmentEntity;

public class AppointmentMapper {
    public AppointmentEntity toJPA(Appointment appointment) {
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