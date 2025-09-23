package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultScheduleAppointmentUseCase implements ScheduleAppointmentUseCase {
    
    private final AppointmentRepository appointmentRepository;
    private final PetServiceRepository petServiceRepository;

    public DefaultScheduleAppointmentUseCase (AppointmentRepository appointmentRepository, PetServiceRepository petServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petServiceRepository = petServiceRepository;
    }

    @Override
    public ScheduleAppointmentResult execute (ScheduleAppointmentCommand command) {
        validations(command);
        int serviceDuration = petServiceRepository.durationById(command.getServiceId());
        Appointment appointment = new Appointment(command.getPetId(),
                                                command.getTutorId(),
                                                command.getProfessionalId(),
                                                command.getServiceId(),
                                                command.getStartAt(),
                                                serviceDuration,
                                                AppointmentStatus.SCHEDULED,
                                                command.getObservation()
                                                );
        appointmentRepository.save(appointment);

        ScheduleAppointmentResult result = new ScheduleAppointmentResult(appointment.getId(),
                                                                        appointment.getServiceId(),
                                                                        appointment.getProfessionalId(),
                                                                        appointment.getStartAt(),
                                                                        appointment.getStartAt().plusMinutes(serviceDuration),
                                                                        appointment.getStatus()
                                                                        );
        return (result);

    }

    private void validations(ScheduleAppointmentCommand command) {
        if (command.getServiceId() == null || command.getServiceId() <= 0)
            throw new IllegalArgumentException("Serviço inexistente");
        if (command.getPetId() == null || command.getPetId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um pet");
        if (command.getTutorId() == null || command.getTutorId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um tutor");
        if (command.getProfessionalId() == null || command.getProfessionalId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um profissional");
        if (command.getServiceId() == null || command.getServiceId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um serviço");
        if (command.getStartAt() == null || command.getStartAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Horário de agendamento da consulta deve ser futuro");

    }
    
}

