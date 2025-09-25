package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;

public interface AppointmentRepository {
    Appointment save (Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByTutorId(Long id);
    Boolean existsOverlapForProfessional(Long professionalId, LocalDateTime start,  LocalDateTime end);
    Boolean existsOverlapForPet(Long petId, LocalDateTime start,  LocalDateTime end);
    List<Appointment> listByProfessionalBetween(Long professionalId, LocalDateTime start, LocalDateTime end);
    Boolean existsOverlapForProfessionalExcluding(Long appointmentId, Long professionalId, LocalDateTime start, LocalDateTime end);
    Boolean existsOverlapForPetExcluding(Long appointmentId, Long petId, LocalDateTime start, LocalDateTime end);
    Boolean existsOwnership(Long tutorId, Long appointmentId);
}