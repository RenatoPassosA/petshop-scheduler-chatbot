package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;

@Service
public class DefaultRescheduleAppointmentUseCase implements RescheduleAppointmentUseCase{

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalTimeOffRepository professionalTimeOffRepository;
    private final BusinessHoursPolicy businessHoursPolicy;
    private final TimeProvider timeProvider;

    public DefaultRescheduleAppointmentUseCase (AppointmentRepository appointmentRepository,
                                                ProfessionalTimeOffRepository professionalTimeOffRepository,
                                                BusinessHoursPolicy businessHoursPolicy,
                                                TimeProvider timeProvider) {
        this.appointmentRepository = appointmentRepository;
        this.professionalTimeOffRepository = professionalTimeOffRepository;
        this.businessHoursPolicy = businessHoursPolicy;
        this.timeProvider = timeProvider;
    }


    @Override
    @Transactional
    public RescheduleAppointmentResult execute (RescheduleAppointmentCommand command) {
        validations(command);
        Appointment appointment = loadExistingAppointment(command);
        OffsetDateTime newStartAt = command.getNewStartAt();
        OffsetDateTime newEndAt = command.getNewStartAt().plusMinutes(appointment.getServiceDuration());
        checkBusinessHours(newStartAt, newEndAt);
        checkTimeOff(appointment, newStartAt, newEndAt);
        checkSchedule(appointment, newStartAt, newEndAt);
        appointment.rescheduleTo(newStartAt, timeProvider.nowInUTC());

        appointment = appointmentRepository.save(appointment);

        RescheduleAppointmentResult result = new RescheduleAppointmentResult(appointment.getId(),
                                                                            appointment.getServiceId(),
                                                                            appointment.getProfessionalId(),
                                                                            appointment.getStartAt(),
                                                                            appointment.getEndAt(),
                                                                            appointment.getStatus());

        return (result);
    }

    private void validations(RescheduleAppointmentCommand command) {
        if (command.getAppointmentId() == null || command.getAppointmentId() <= 0)
            throw new DomainValidationException("Necessário vincular um agendamento");
        if (command.getNewStartAt() == null || command.getNewStartAt().isBefore(timeProvider.nowInUTC()))
            throw new DomainValidationException("Data do novo agendamento inválida");
    }

    private Appointment loadExistingAppointment(RescheduleAppointmentCommand command) {
    
        Optional<Appointment> findAppointment = appointmentRepository.findById(command.getAppointmentId());
        if (findAppointment.isEmpty()) 
            throw new AppointmentNotFoundException("Agendamento não encontrado");
        AppointmentStatus status = findAppointment.get().getStatus();
        if (status == AppointmentStatus.CANCELED || status == AppointmentStatus.COMPLETED)
            throw new InvalidAppointmentStateException("Agendamento já cancelado ou concluído");
        return (findAppointment.get());
    }

    private void checkBusinessHours(OffsetDateTime newStartAt, OffsetDateTime newEndAt) {
        if (!businessHoursPolicy.fits(newStartAt, newEndAt))
            throw new WorkingHoursOutsideException("Horário fora do expediente");
    }
  
    private void checkTimeOff(Appointment appointment, OffsetDateTime newStartAt, OffsetDateTime newEndAt) {
        Long professionalId = appointment.getProfessionalId();
        
        if (professionalTimeOffRepository.existsOverlap(professionalId, newStartAt, newEndAt))
            throw new ProfessionalTimeOffException("Profissional está em folga");
    }

    private void checkSchedule(Appointment appointment, OffsetDateTime newStartAt, OffsetDateTime newEndAt) {
        Long appointmentId = appointment.getId();
        Long professionalId = appointment.getProfessionalId();
        Long petId = appointment.getPetId();
        
        if (appointmentRepository.existsOverlapForProfessionalExcluding(professionalId, newStartAt, newEndAt, appointmentId))
            throw new AppointmentOverlapException("Horário do profissional indisponível");
        if (appointmentRepository.existsOverlapForPetExcluding(petId, newStartAt, newEndAt, appointmentId))
            throw new PetOverlapException("Pet com serviço agendado no mesmo horário");
    }
}
