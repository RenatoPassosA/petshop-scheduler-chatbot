package com.project.petshop_scheduler_chatbot.application.appointment.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@Service
public class DefaultCancelAppointmentUseCase implements CancelAppointmentUseCase{
    
    private final AppointmentRepository appointmentRepository;
    private final PetServiceRepository petServiceRepository;

    public DefaultCancelAppointmentUseCase (AppointmentRepository appointmentRepository, PetServiceRepository petServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petServiceRepository = petServiceRepository;
    }

    @Override
    public CancelAppointmentResult execute (CancelAppointmentCommand command) {
        validations(command);
        Appointment appointment = loadExistingAppointment(command);
        appointment.setStatus(AppointmentStatus.CANCELED);
        String serviceName = getServiceName(appointment);
        appointmentRepository.save(appointment);

        CancelAppointmentResult result = new CancelAppointmentResult(appointment.getId(),
                                                                        serviceName,
                                                                        appointment.getStatus());

        return (result);

    }

    private void validations(CancelAppointmentCommand command) {
        if (command.getAppointmentId() == null || command.getAppointmentId() <= 0)
            throw new DomainValidationException("Necessário vincular um agendamento");
    }

    private Appointment loadExistingAppointment(CancelAppointmentCommand command) {
    
        Optional<Appointment> findAppointment = appointmentRepository.findById(command.getAppointmentId());
        if (findAppointment.isEmpty()) 
            throw new AppointmentNotFoundException("Agendamento não encontrado");
        if (findAppointment.get().getStatus() == AppointmentStatus.CANCELED ||
            findAppointment.get().getStatus() == AppointmentStatus.DONE)
            throw new InvalidAppointmentStateException("Agendamento já cancelado ou concluído");
        return (findAppointment.get());
    }

    private String getServiceName(Appointment appointment) {
        Optional<PetService> petService = petServiceRepository.findById(appointment.getServiceId());
        if (petService.isEmpty())
            throw new ServiceNotFoundException("Não foi possível encontrar o serviço");
        return (petService.get().getName());
    }
}
