package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalWorkingHoursRepository;

@Service
public class DefaultRescheduleAppointmentUseCase implements RescheduleAppointmentUseCase{

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalWorkingHoursRepository professionalWorkingHoursRepository;
    private final ProfessionalTimeOffRepository professionalTimeOffRepository;

    public DefaultRescheduleAppointmentUseCase (AppointmentRepository appointmentRepository,
                                                ProfessionalWorkingHoursRepository professionalWorkingHoursRepository,
                                                ProfessionalTimeOffRepository professionalTimeOffRepository) {
        this.appointmentRepository = appointmentRepository;
        this.professionalWorkingHoursRepository = professionalWorkingHoursRepository;
        this.professionalTimeOffRepository = professionalTimeOffRepository;
    }


    @Override
    public RescheduleAppointmentResult execute (RescheduleAppointmentCommand command) {
        validations(command);
        Appointment appointment = loadExistingAppointment(command);
        LocalDateTime newStartAt = command.getNewStartAt();
        LocalDateTime newEndAt = command.getNewStartAt().plusMinutes(appointment.getServiceDuration());
        checkWorkingHours(appointment, newStartAt, newEndAt);
        checkTimeOff(appointment, newStartAt, newEndAt);
        checkSchedule(appointment, newStartAt, newEndAt);
        appointment.setStartAt(newStartAt);

        appointmentRepository.save(appointment);

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
        if (command.getNewStartAt() == null || command.getNewStartAt().isBefore(LocalDateTime.now()))
            throw new DomainValidationException("Data do novo agendamento inválida");
    }

    private Appointment loadExistingAppointment(RescheduleAppointmentCommand command) {
    
        Optional<Appointment> findAppointment = appointmentRepository.findById(command.getAppointmentId());
        if (findAppointment.isEmpty()) 
            throw new AppointmentNotFoundException("Agendamento não encontrado");
        AppointmentStatus status = findAppointment.get().getStatus();
        if (status == AppointmentStatus.CANCELED || status == AppointmentStatus.DONE)
            throw new InvalidAppointmentStateException("Agendamento já cancelado ou concluído");
        return (findAppointment.get());
    }

    private void checkWorkingHours(Appointment appointment, LocalDateTime newStartAt, LocalDateTime newEndAt) {
        
        Long professionalId = appointment.getProfessionalId();
        
        if (!professionalWorkingHoursRepository.existsWindow(professionalId, newStartAt, newEndAt))
            throw new WorkingHoursOutsideException("Horário fora da janela de trabalho do profissional");
    }

    private void checkTimeOff(Appointment appointment, LocalDateTime newStartAt, LocalDateTime newEndAt) {
        Long professionalId = appointment.getProfessionalId();
        
        if (professionalTimeOffRepository.isInTimeOff(professionalId, newStartAt, newEndAt))
            throw new ProfessionalTimeOffException("Profissional está em folga");
    }

    private void checkSchedule(Appointment appointment, LocalDateTime newStartAt, LocalDateTime newEndAt) {
        Long appointmentId = appointment.getId();
        Long professionalId = appointment.getProfessionalId();
        Long petId = appointment.getPetId();
        
        if (appointmentRepository.existsOverlapForProfessionalExcluding(appointmentId, professionalId, newStartAt, newEndAt))
            throw new AppointmentOverlapException("Horário do profissional indisponível");
        if (appointmentRepository.existsOverlapForPetExcluding(appointmentId, petId, newStartAt, newEndAt))
            throw new PetOverlapException("Pet com serviço agendado no mesmo horário");
    }
}
