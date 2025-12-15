package com.project.petshop_scheduler_chatbot.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.impl.DefaultCancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@ExtendWith(MockitoExtension.class)
public class CancelAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PetServiceRepository petServiceRepository;

    @Mock
    private TimeProvider timeProvider;

    private DefaultCancelAppointmentUseCase defaultCancelAppointmentUseCase;

    @BeforeEach
    void setUp() {
        defaultCancelAppointmentUseCase = new DefaultCancelAppointmentUseCase(appointmentRepository, petServiceRepository, timeProvider);
    }

    @Test
    public void cancelAppointment_Sucess() {
        Long appointmentId = 1L;
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = OffsetDateTime.parse("2025-12-08T12:30:00Z");
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, startAt, 120, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        Appointment appointmentWithId = appointment.withPersistenceId(appointmentId);
        PetService petService = new PetService("tosa", new BigDecimal(100), 150, Office.AUX, provided, provided);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointmentWithId));
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(appointmentRepository.save(appointmentWithId)).thenReturn(appointmentWithId);

        CancelAppointmentResult result = defaultCancelAppointmentUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(result.getServiceName()).isEqualTo(petService.getName());
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);

        verify(timeProvider, times(1)).nowInUTC();
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(appointmentRepository, times(1)).save(appointmentWithId);
    }


    @Test
    public void cancelAppointment_Fail_DomainValidationException() {
        Long appointmentId = null;
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);

        assertThrows(DomainValidationException.class, () -> {
                defaultCancelAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, never()).findById(anyLong());
        verify(petServiceRepository, never()).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));

    }

    @Test
    public void cancelAppointment_Fail_AppointmentNotFoundException() {
        Long appointmentId = 1L;
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> {
                defaultCancelAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(petServiceRepository, never()).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void cancelAppointment_Fail_InvalidAppointmentStateException() {
        Long appointmentId = 1L;
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 120, AppointmentStatus.COMPLETED, "nenhuma", provided, provided);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(InvalidAppointmentStateException.class, () -> {
                defaultCancelAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(petServiceRepository, never()).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void cancelAppointment_Fail_PetServiceNotFoundException() {
        Long appointmentId = 1L;
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        CancelAppointmentCommand command = new CancelAppointmentCommand(appointmentId);
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 120, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(PetServiceNotFoundException.class, () -> {
                defaultCancelAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}
