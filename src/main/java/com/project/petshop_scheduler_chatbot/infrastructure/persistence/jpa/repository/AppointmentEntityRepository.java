package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.AppointmentEntity;

public interface AppointmentEntityRepository extends JpaRepository <AppointmentEntity, Long> {
    List<AppointmentEntity> findAllByTutorId(Long tutorId);
    List<AppointmentEntity> findAllByProfessionalIdAndStartAtBetweenAndIdNot(Long professionalId, OffsetDateTime from, OffsetDateTime to, Long appointmentId);
    List<AppointmentEntity> findAllByProfessionalIdAndStartAtBetween(Long professionalId, OffsetDateTime from, OffsetDateTime to);
    List<AppointmentEntity> findAllByPetIdAndStartAtBetweenAndIdNot(Long petId, OffsetDateTime from, OffsetDateTime to, Long appointmentId);
    List<AppointmentEntity> findAllByPetIdAndStartAtBetween(Long petId, OffsetDateTime from, OffsetDateTime to);
    boolean existsByIdAndTutorId(Long id, Long tutorId);
    
}
