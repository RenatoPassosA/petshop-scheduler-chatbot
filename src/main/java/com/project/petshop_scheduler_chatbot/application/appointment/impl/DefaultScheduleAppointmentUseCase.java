package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalWorkingHoursRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultScheduleAppointmentUseCase implements ScheduleAppointmentUseCase {
    
    private final AppointmentRepository appointmentRepository;
    private final PetServiceRepository petServiceRepository;
    private final ProfessionalWorkingHoursRepository professionalWorkingHoursRepository;
    private final ProfessionalTimeOffRepository professionalTimeOffRepository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final ProfessionalRepository professionalRepository;

    public DefaultScheduleAppointmentUseCase (AppointmentRepository appointmentRepository,
                                            PetServiceRepository petServiceRepository,
                                            ProfessionalWorkingHoursRepository professionalWorkingHoursRepository,
                                            ProfessionalTimeOffRepository professionalTimeOffRepository,
                                            PetRepository petRepository,
                                            TutorRepository tutorRepository,
                                            ProfessionalRepository professionalRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petServiceRepository = petServiceRepository;
        this.professionalWorkingHoursRepository = professionalWorkingHoursRepository;
        this.professionalTimeOffRepository = professionalTimeOffRepository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.professionalRepository = professionalRepository;
    }

    @Override
    public ScheduleAppointmentResult execute (ScheduleAppointmentCommand command) {
        validations(command);
        Optional<PetService> petService = getPetServiceInstance(command);
        int duration = getDuration(petService);
        LocalDateTime end = command.getStartAt().plusMinutes(duration);
        checkWorkingHours(command, end);
        checkTimeOff(command, end);
        checkSchedule(command, end);
        Appointment appointment = new Appointment(command.getPetId(),
                                                command.getTutorId(),
                                                command.getProfessionalId(),
                                                command.getServiceId(),
                                                command.getStartAt(),
                                                duration,
                                                AppointmentStatus.SCHEDULED,
                                                command.getObservation()
                                                );
        appointmentRepository.save(appointment);

        ScheduleAppointmentResult result = new ScheduleAppointmentResult(appointment.getId(),
                                                                        appointment.getServiceId(),
                                                                        appointment.getProfessionalId(),
                                                                        petService.get().getName(),
                                                                        appointment.getStartAt(),
                                                                        end,
                                                                        appointment.getStatus()
                                                                        );
        return (result);

    }

    private void validations(ScheduleAppointmentCommand command) {
        if (command.getServiceId() == null || command.getServiceId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um serviço");
        if (command.getPetId() == null || command.getPetId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um pet");
        if (command.getTutorId() == null || command.getTutorId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um tutor");
        if (command.getProfessionalId() == null || command.getProfessionalId() <= 0)
            throw new IllegalArgumentException("Necessário vincular um profissional");
        if (command.getStartAt() == null || command.getStartAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Horário de agendamento da consulta deve estar no futuro");
        if (!tutorRepository.existsById(command.getTutorId()))
            throw new IllegalArgumentException("Tutor inválido");
        if (!professionalRepository.existsById(command.getProfessionalId()))
            throw new IllegalArgumentException("Profissional inválido");
        if (!petRepository.existsByIdAndTutorId(command.getPetId(), command.getTutorId()))
            throw new IllegalArgumentException("Pet não pertence ao tutor");
    }

    private void checkWorkingHours(ScheduleAppointmentCommand command, LocalDateTime end) {
        Long professionalId = command.getProfessionalId();
        LocalDateTime start = command.getStartAt();
        if (!professionalWorkingHoursRepository.existsWindow(professionalId, start, end))
            throw new IllegalArgumentException("Horário fora da janela de trabalho do profissional");
    }

    private void checkTimeOff(ScheduleAppointmentCommand command, LocalDateTime end) {
        Long professionalId = command.getProfessionalId();
        LocalDateTime start = command.getStartAt();
        if (professionalTimeOffRepository.isInTimeOff(professionalId, start, end))
            throw new IllegalArgumentException("Profissional está Off");
    }

    private void checkSchedule(ScheduleAppointmentCommand command, LocalDateTime end) {
        Long professionalId = command.getProfessionalId();
        Long petId = command.getPetId();
        LocalDateTime start = command.getStartAt();
        if (appointmentRepository.existsOverlapForProfessional(professionalId, start, end))
            throw new IllegalArgumentException("Horário do profissional indisponível");
        if (appointmentRepository.existsOverlapForPet(petId, start, end))
            throw new IllegalArgumentException("Pet com serviço agendado no mesmo horário");
    }

    private Optional<PetService> getPetServiceInstance(ScheduleAppointmentCommand command) {
        Optional<PetService> petService = petServiceRepository.findById(command.getServiceId());
        if (petService.isEmpty())
            throw new IllegalArgumentException("Não foi possível encontrar o serviço");
        return (petService);
    }

    private int getDuration(Optional<PetService> petService) {
        int duration = petService.get().getDuration();
        if (duration <= 30)
            throw new IllegalArgumentException("Serviço com duração inválida");
        return (duration);
    }
}