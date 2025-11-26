package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.AppointmentEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.AppointmentMapper;

@Repository
public class AppointmentRepositoryJpa implements AppointmentRepository {
    final private AppointmentMapper appointmentMapper;
    final private AppointmentEntityRepository appointmentEntityRepository;
    final long maxDurationMinutes;

    public AppointmentRepositoryJpa (AppointmentMapper appointmentMapper, AppointmentEntityRepository appointmentEntityRepository, @Value("${scheduling.max-duration-minutes:180}") long maxDurationMinutes) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentEntityRepository = appointmentEntityRepository;
        this.maxDurationMinutes = maxDurationMinutes;
    }

    @Override
    public Appointment save (Appointment appointment) {
        AppointmentEntity persistence = appointmentMapper.toJPA(appointment);
        persistence = appointmentEntityRepository.save(persistence);
        appointment = appointment.withPersistenceId(persistence.getId());
        return (appointment);
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return (appointmentEntityRepository
            .findById(id)
            .map(appointmentMapper::toDomain));
    }

    @Override
    public List<Appointment> findByTutorId(Long id) {
        return (appointmentEntityRepository
            .findAllByTutorId(id).stream()
            .map(appointmentMapper::toDomain)
            .toList());
    }

    @Override
    public boolean existsOverlapForProfessional(Long professionalId, OffsetDateTime start,  OffsetDateTime end) {
        OffsetDateTime from = start.minusMinutes(maxDurationMinutes);
        List<AppointmentEntity> candidates = appointmentEntityRepository.findAllByProfessionalIdAndStartAtBetween(professionalId, from, end);
        
        for (AppointmentEntity appointment : candidates) {
            OffsetDateTime existingStart = appointment.getStartAt();
            OffsetDateTime existingEnd   = existingStart.plusMinutes(appointment.getServiceDurationMinutes());

            boolean overlap = existingStart.isBefore(end) && existingEnd.isAfter(start);
            if (overlap)
                return (true);
        }
        return false;  

    }

    @Override
    public boolean existsOverlapForProfessionalExcluding(Long professionalId, OffsetDateTime start,  OffsetDateTime end, Long appointmentId) {
        OffsetDateTime from = start.minusMinutes(maxDurationMinutes);
        List<AppointmentEntity> candidates = appointmentEntityRepository.findAllByProfessionalIdAndStartAtBetweenAndIdNot(professionalId, from, end, appointmentId);
        
        for (AppointmentEntity appointment : candidates) {
            OffsetDateTime existingStart = appointment.getStartAt();
            OffsetDateTime existingEnd   = existingStart.plusMinutes(appointment.getServiceDurationMinutes());
    
            boolean overlap = existingStart.isBefore(end) && existingEnd.isAfter(start);
            if (overlap)
                return (true);
        }
        return false;  
    }

    @Override
    public boolean existsOverlapForPet(Long petId, OffsetDateTime start, OffsetDateTime end) {
        OffsetDateTime from = start.minusMinutes(maxDurationMinutes);
        List<AppointmentEntity> candidates = appointmentEntityRepository.findAllByPetIdAndStartAtBetween(petId, from, end);

        for (AppointmentEntity appointment : candidates) {
            OffsetDateTime existingStart = appointment.getStartAt();
            OffsetDateTime existingEnd   = existingStart.plusMinutes(appointment.getServiceDurationMinutes());
    
            boolean overlap = existingStart.isBefore(end) && existingEnd.isAfter(start);
            if (overlap)
                return (true);
        }
        return (false);
    }

    @Override
     public boolean existsOverlapForPetExcluding(Long petId, OffsetDateTime start, OffsetDateTime end, Long appointmentId) {
        OffsetDateTime from = start.minusMinutes(maxDurationMinutes);
        List<AppointmentEntity> candidates = appointmentEntityRepository.findAllByPetIdAndStartAtBetweenAndIdNot(petId, from, end, appointmentId);

        for (AppointmentEntity appointment : candidates) {
            OffsetDateTime existingStart = appointment.getStartAt();
            OffsetDateTime existingEnd   = existingStart.plusMinutes(appointment.getServiceDurationMinutes());
    
            boolean overlap = existingStart.isBefore(end) && existingEnd.isAfter(start);
            if (overlap)
                return (true);
        }
        return (false);
    }

    @Override
    public boolean existsOwnership(Long tutorId, Long appointmentId) {
        return (appointmentEntityRepository.existsByIdAndTutorId(appointmentId, tutorId));
    }




}
