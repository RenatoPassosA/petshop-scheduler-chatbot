package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;

public interface AppointmentRepository {
    Appointment save (Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByTutorId(Long id);
    Boolean existsOverlapForProfessional(Long professionalId, OffsetDateTime start,  OffsetDateTime end);
    Boolean existsOverlapForPet(Long petId, OffsetDateTime start,  OffsetDateTime end);
    List<Appointment> listByProfessionalBetween(Long professionalId, OffsetDateTime start, OffsetDateTime end);
    Boolean existsOverlapForProfessionalExcluding(Long appointmentId, Long professionalId, OffsetDateTime start, OffsetDateTime end);
    Boolean existsOverlapForPetExcluding(Long appointmentId, Long petId, OffsetDateTime start, OffsetDateTime end);
    Boolean existsOwnership(Long tutorId, Long appointmentId);
}