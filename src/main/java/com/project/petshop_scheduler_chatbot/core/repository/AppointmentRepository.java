package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;

public interface AppointmentRepository {
    Appointment save (Appointment appointment);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByTutorId(Long id);
    Boolean existsOverlap(Long professionalId, LocalDateTime start,  LocalDateTime end);
    List<Appointment> listByProfessionalBetween(Long professionalId, LocalDateTime start, LocalDateTime end);
}
