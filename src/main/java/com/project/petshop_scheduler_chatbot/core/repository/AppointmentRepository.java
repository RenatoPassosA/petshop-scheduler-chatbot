package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;

public interface AppointmentRepository {
    Appointment save (Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByTutorId(Long id);
    boolean existsOverlapForProfessional(Long professionalId, OffsetDateTime start,  OffsetDateTime end);
    boolean existsOverlapForProfessionalExcluding(Long professionalId, OffsetDateTime start, OffsetDateTime end, Long appointmentId);
    boolean existsOverlapForPet(Long petId, OffsetDateTime start, OffsetDateTime end);
    boolean existsOverlapForPetExcluding(Long petId, OffsetDateTime start, OffsetDateTime end, Long appointmentId);
    boolean existsOwnership(Long tutorId, Long appointmentId);
    List<Appointment> listByProfessionalBetween(Long professionalId, OffsetDateTime start, OffsetDateTime end);
}