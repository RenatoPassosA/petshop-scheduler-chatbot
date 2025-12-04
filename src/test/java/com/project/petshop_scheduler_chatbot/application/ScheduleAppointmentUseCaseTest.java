package com.project.petshop_scheduler_chatbot.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.impl.DefaultScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.PetOverlapException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.repository.AppointmentRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
public class ScheduleAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PetServiceRepository petServiceRepository;

    @Mock
    private ProfessionalTimeOffRepository professionalTimeOffRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private BusinessHoursPolicy businessHoursPolicy;

    @Mock
    private TimeProvider timeProvider;

    private DefaultScheduleAppointmentUseCase defaultScheduleAppointmentUseCase;

    @BeforeEach
    void setUp() {
        defaultScheduleAppointmentUseCase = new DefaultScheduleAppointmentUseCase(appointmentRepository,
                                                                                petServiceRepository,
                                                                                professionalTimeOffRepository,
                                                                                petRepository,
                                                                                tutorRepository,
                                                                                professionalRepository,
                                                                                businessHoursPolicy,
                                                                                timeProvider);
    }

    @Test
    public void scheduleAppointment_Sucess() {
        Long appointmentId = 1L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = provided.plusMinutes(1);
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, startAt, "ouvido inflamado");
        int duration = 180;
        PetService petService = new PetService("tosa", new BigDecimal(100), duration, provided, provided);
        OffsetDateTime endAt = startAt.plusMinutes(petService.getDuration());
        Appointment appointmentWithId = new Appointment(petId,tutorId, professionalId, serviceId, startAt, duration, AppointmentStatus.SCHEDULED, "ouvido inflamado", provided, provided)
        .withPersistenceId(appointmentId);

        when(timeProvider.nowInUTC()).thenReturn(provided, provided, provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessional(professionalId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForPet(petId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointmentWithId);

        ScheduleAppointmentResult result = defaultScheduleAppointmentUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(result.getServiceId()).isEqualTo(serviceId);
        assertThat(result.getProfessionalId()).isEqualTo(professionalId);
        assertThat(result.getServiceName()).isEqualTo(petService.getName());
        assertThat(result.getStartAt()).isEqualTo(startAt);
        assertThat(result.getEndAt()).isEqualTo(endAt);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);

        verify(timeProvider, times(3)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessional(professionalId, startAt, endAt);
        verify(appointmentRepository, times(1)).existsOverlapForPet(petId, startAt, endAt);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));

    }

    @Test
    public void scheduleAppointment_Fail_ServiceNull_ServiceNotFoundException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = null;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided, "ouvido inflamado");

        assertThrows(ServiceNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verifyNoInteractions(timeProvider);
        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_PetNull_PetNotFoundException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = null;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided, "ouvido inflamado");

        assertThrows(PetNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verifyNoInteractions(timeProvider);
        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_TutorNull_TutorNotFoundException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = null;
        Long professionalId = 4L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided, "ouvido inflamado");

        assertThrows(TutorNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verifyNoInteractions(timeProvider);
        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_ProfessionalNull_ProfessionalNotFoundException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = null;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided, "ouvido inflamado");

        assertThrows(ProfessionalNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verifyNoInteractions(timeProvider);
        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_PastDateSchedule_DomainValidationException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime pastDate = OffsetDateTime.parse("2024-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, pastDate, "ouvido inflamado");

        when(timeProvider.nowInUTC()).thenReturn(provided);

        assertThrows(DomainValidationException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_TutorNotFound() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided.plusMinutes(1), "ouvido inflamado");

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(false);
        
        assertThrows(TutorNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verifyNoInteractions(professionalRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_ProfessionalNotFound() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided.plusMinutes(1), "ouvido inflamado");

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(false);
        
        assertThrows(ProfessionalNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_PetDoesntMatchTutor() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided.plusMinutes(1), "ouvido inflamado");

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(false);
        
        assertThrows(DomainValidationException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verifyNoInteractions(petServiceRepository);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_ServiceNotFound() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided.plusMinutes(1), "ouvido inflamado");

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.empty());
        
        assertThrows(ServiceNotFoundException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_WrongDuration() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, provided.plusMinutes(1), "ouvido inflamado");
        PetService petService = new PetService("tosa", new BigDecimal(100), 20, provided, provided);


        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        
        assertThrows(DomainValidationException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verifyNoInteractions(businessHoursPolicy);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_WorkingHoursOutsideExceptionException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = provided.plusMinutes(1);
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, startAt, "ouvido inflamado");
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        OffsetDateTime endAt = startAt.plusMinutes(petService.getDuration());


        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(false);
        
        assertThrows(WorkingHoursOutsideException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verifyNoInteractions(professionalTimeOffRepository);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_ProfessionalTimeOffException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = provided.plusMinutes(1);
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, startAt, "ouvido inflamado");
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        OffsetDateTime endAt = startAt.plusMinutes(petService.getDuration());


        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(true);
        
        assertThrows(ProfessionalTimeOffException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verifyNoInteractions(appointmentRepository);       
    }

    @Test
    public void scheduleAppointment_Fail_AppointmentOverlapException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = provided.plusMinutes(1);
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, startAt, "ouvido inflamado");
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        OffsetDateTime endAt = startAt.plusMinutes(petService.getDuration());


        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessional(professionalId, startAt, endAt)).thenReturn(true);
        
        assertThrows(AppointmentOverlapException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessional(professionalId, startAt, endAt);
        verify(appointmentRepository, never()).existsOverlapForPet(petId, startAt, endAt);
    }

    @Test
    public void scheduleAppointment_Fail_PetOverlapException() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime startAt = provided.plusMinutes(1);
        Long petId = 1L;
        Long tutorId = 2L;
        Long professionalId = 3L;
        Long serviceId = 5L;
        ScheduleAppointmentCommand command = new ScheduleAppointmentCommand(petId, tutorId, professionalId, serviceId, startAt, "ouvido inflamado");
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        OffsetDateTime endAt = startAt.plusMinutes(petService.getDuration());


        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(petRepository.existsByIdAndTutorId(petId, tutorId)).thenReturn(true);
        when(petServiceRepository.findById(serviceId)).thenReturn(Optional.of(petService));
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForProfessional(professionalId, startAt, endAt)).thenReturn(false);
        when(appointmentRepository.existsOverlapForPet(petId, startAt, endAt)).thenReturn(true);
        
        assertThrows(PetOverlapException.class, () -> {
            defaultScheduleAppointmentUseCase.execute(command);
            });

        verify(timeProvider, times(1)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(petRepository, times(1)).existsByIdAndTutorId(petId, tutorId);
        verify(petServiceRepository, times(1)).findById(serviceId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verify(appointmentRepository, times(1)).existsOverlapForProfessional(professionalId, startAt, endAt);
        verify(appointmentRepository, times(1)).existsOverlapForPet(petId, startAt, endAt);
    }







}
