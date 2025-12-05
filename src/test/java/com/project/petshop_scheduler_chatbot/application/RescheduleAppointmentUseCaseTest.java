package com.project.petshop_scheduler_chatbot.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.impl.DefaultRescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;

@ExtendWith(MockitoExtension.class)
public class RescheduleAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ProfessionalTimeOffRepository professionalTimeOffRepository;

    @Mock
    private BusinessHoursPolicy businessHoursPolicy;

    @Mock
    private TimeProvider timeProvider;

    private DefaultRescheduleAppointmentUseCase defaultRescheduleAppointmentUseCase;

    @BeforeEach
    void setUp() {
        defaultRescheduleAppointmentUseCase = new DefaultRescheduleAppointmentUseCase(appointmentRepository, professionalTimeOffRepository, businessHoursPolicy, timeProvider);
    }

    @Test
    public void rescheduleAppointment_Sucess() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        OffsetDateTime newEndAt = provided.plusMinutes(appointment.getServiceDuration());
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(businessHoursPolicy.fits(provided, newEndAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, provided, newEndAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId)).thenReturn(false);
        when(appointmentRepository.existsOverlapForPetExcluding(petId, provided, newEndAt, appointmentId)).thenReturn(false);

        RescheduleAppointmentResult result = defaultRescheduleAppointmentUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(result.getServiceId()).isEqualTo(serviceId);
        assertThat(result.getProfessionalId()).isEqualTo(professionalId);
        assertThat(result.getStartAt()).isEqualTo(provided);
        assertThat(result.getEndAt()).isEqualTo(provided.plusMinutes(appointment.getServiceDuration()));
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, times(1)).fits(provided, newEndAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, provided, newEndAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId);
        verify(appointmentRepository, times(1)).existsOverlapForPetExcluding(petId, provided, newEndAt, appointmentId);
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    public void rescheduleAppointment_Fail_DomainValidationException() {
        Long appointmentId = null;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        assertThrows(DomainValidationException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, never()).findById(anyLong());
        verify(businessHoursPolicy, never()).fits(provided, provided);
        verify(professionalTimeOffRepository, never()).existsOverlap(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class));
        verify(appointmentRepository, never()).existsOverlapForProfessionalExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_Fail_AppointmentNotFoundException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, never()).fits(provided, provided);
        verify(professionalTimeOffRepository, never()).existsOverlap(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class));
        verify(appointmentRepository, never()).existsOverlapForProfessionalExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_Fail_InvalidAppointmentStateException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.CANCELLED, "nenhuma", provided, provided);
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(InvalidAppointmentStateException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, never()).fits(provided, provided);
        verify(professionalTimeOffRepository, never()).existsOverlap(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class));
        verify(appointmentRepository, never()).existsOverlapForProfessionalExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_Fail_WorkingHoursOutsideException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        OffsetDateTime newEndAt = provided.plusMinutes(appointment.getServiceDuration());
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(businessHoursPolicy.fits(provided, newEndAt)).thenReturn(false);


        assertThrows(WorkingHoursOutsideException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, times(1)).fits(provided, newEndAt);
        verify(professionalTimeOffRepository, never()).existsOverlap(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class));
        verify(appointmentRepository, never()).existsOverlapForProfessionalExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_Fail_ProfessionalTimeOffException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        OffsetDateTime newEndAt = provided.plusMinutes(appointment.getServiceDuration());
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(businessHoursPolicy.fits(provided, newEndAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, provided, newEndAt)).thenReturn(true);

        assertThrows(ProfessionalTimeOffException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, times(1)).fits(provided, newEndAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, provided, newEndAt);
        verify(appointmentRepository, never()).existsOverlapForProfessionalExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }


    @Test
    public void rescheduleAppointment_Fail_AppointmentOverlapException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        OffsetDateTime newEndAt = provided.plusMinutes(appointment.getServiceDuration());
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(businessHoursPolicy.fits(provided, newEndAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, provided, newEndAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId)).thenReturn(true);


        assertThrows(AppointmentOverlapException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, times(1)).fits(provided, newEndAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, provided, newEndAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId);
        verify(appointmentRepository, never()).existsOverlapForPetExcluding(anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    public void rescheduleAppointment_Fail_PetOverlapException() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, provided, 100, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
        OffsetDateTime newEndAt = provided.plusMinutes(appointment.getServiceDuration());
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(appointmentId, provided);

        when(timeProvider.nowInUTC()).thenReturn(provided.minusMinutes(1), provided);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(businessHoursPolicy.fits(provided, newEndAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, provided, newEndAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId)).thenReturn(false);
        when(appointmentRepository.existsOverlapForPetExcluding(petId, provided, newEndAt, appointmentId)).thenReturn(true);

        assertThrows(PetOverlapException.class, () -> {
            defaultRescheduleAppointmentUseCase.execute(command);
            });

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(businessHoursPolicy, times(1)).fits(provided, newEndAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, provided, newEndAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessionalExcluding(professionalId, provided, newEndAt, appointmentId);
        verify(appointmentRepository, times(1)).existsOverlapForPetExcluding(petId, provided, newEndAt, appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}