package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@Service
public class DefaultScheduleAppointmentUseCase implements ScheduleAppointmentUseCase {
    
    private final AppointmentRepository appointmentRepository;
    private final PetServiceRepository petServiceRepository;
    private final ProfessionalTimeOffRepository professionalTimeOffRepository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final ProfessionalRepository professionalRepository;
    private final BusinessHoursPolicy businessHoursPolicy;
    private final TimeProvider timeProvider;

    public DefaultScheduleAppointmentUseCase (AppointmentRepository appointmentRepository,
                                            PetServiceRepository petServiceRepository,
                                            ProfessionalTimeOffRepository professionalTimeOffRepository,
                                            PetRepository petRepository,
                                            TutorRepository tutorRepository,
                                            ProfessionalRepository professionalRepository,
                                            BusinessHoursPolicy businessHoursPolicy,
                                            TimeProvider timeProvider) {
        this.appointmentRepository = appointmentRepository;
        this.petServiceRepository = petServiceRepository;
        this.professionalTimeOffRepository = professionalTimeOffRepository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.professionalRepository = professionalRepository;
        this.businessHoursPolicy = businessHoursPolicy;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public ScheduleAppointmentResult execute (ScheduleAppointmentCommand command) {
        validations(command);
        PetService petService = getPetServiceInstance(command);
        int duration = getDuration(petService);
        OffsetDateTime end = command.getStartAt().plusMinutes(duration);
        checkBusinessHours(command.getStartAt(), end);
        checkTimeOff(command, end);
        checkSchedule(command, end);
        Appointment appointment = new Appointment(command.getPetId(),
                                                command.getTutorId(),
                                                command.getProfessionalId(),
                                                command.getServiceId(),
                                                command.getStartAt(),
                                                duration,
                                                AppointmentStatus.SCHEDULED,
                                                command.getObservation(),
                                                this.timeProvider.nowInUTC(),
                                                this.timeProvider.nowInUTC()
                                                );
        appointment = appointmentRepository.save(appointment);

        ScheduleAppointmentResult result = new ScheduleAppointmentResult(appointment.getId(),
                                                                        appointment.getServiceId(),
                                                                        appointment.getProfessionalId(),
                                                                        petService.getName(),
                                                                        appointment.getStartAt(),
                                                                        end,
                                                                        appointment.getStatus()
                                                                        );
        return (result);

    }

    private void validations(ScheduleAppointmentCommand command) {
        if (command.getServiceId() == null || command.getServiceId() <= 0)
            throw new DomainValidationException("Necessário vincular um serviço");
        if (command.getPetId() == null || command.getPetId() <= 0)
            throw new DomainValidationException("Necessário vincular um pet");
        if (command.getTutorId() == null || command.getTutorId() <= 0)
            throw new DomainValidationException("Necessário vincular um tutor");
        if (command.getProfessionalId() == null || command.getProfessionalId() <= 0)
            throw new ProfessionalNotFoundException("Necessário vincular um profissional");
        if (command.getStartAt() == null || command.getStartAt().isBefore(timeProvider.nowInUTC()))
            throw new DomainValidationException("Horário de agendamento da consulta deve estar no futuro");
        if (!tutorRepository.existsById(command.getTutorId()))
            throw new TutorNotFoundException("Tutor inválido");
        if (!professionalRepository.existsById(command.getProfessionalId()))
            throw new ProfessionalNotFoundException("Profissional inválido");
        if (!petRepository.existsByIdAndTutorId(command.getPetId(), command.getTutorId()))
            throw new DomainValidationException("Pet não pertence ao tutor");
    }

    private void checkBusinessHours(OffsetDateTime start, OffsetDateTime end) {
        if (!businessHoursPolicy.fits(start, end))
            throw new WorkingHoursOutsideException("Horário fora do expediente");
    }

    private void checkTimeOff(ScheduleAppointmentCommand command, OffsetDateTime end) {
        Long professionalId = command.getProfessionalId();
        OffsetDateTime start = command.getStartAt();
        if (professionalTimeOffRepository.existsOverlap(professionalId, start, end))
            throw new ProfessionalTimeOffException("Profissional está de folga");
    }

    private void checkSchedule(ScheduleAppointmentCommand command, OffsetDateTime end) {
        Long professionalId = command.getProfessionalId();
        Long petId = command.getPetId();
        OffsetDateTime start = command.getStartAt();
        if (appointmentRepository.existsOverlapForProfessional(professionalId, start, end))
            throw new AppointmentOverlapException("Horário do profissional indisponível");
        if (appointmentRepository.existsOverlapForPet(petId, start, end))
            throw new PetOverlapException("Pet com serviço agendado no mesmo horário");
    }

    private PetService getPetServiceInstance(ScheduleAppointmentCommand command) {
        Optional<PetService> petService = petServiceRepository.findById(command.getServiceId());
        if (petService.isEmpty())
            throw new ServiceNotFoundException("Não foi possível encontrar o serviço");
        return (petService.get());
    }

    private int getDuration(PetService petService) {
        int duration = petService.getDuration();
        if (duration < 30)
            throw new DomainValidationException("Serviço com duração inválida");
        return (duration);
    }
}