package com.project.petshop_scheduler_chatbot.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.application.professional.TimeOffUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.impl.DefaultTimeOffUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.policy.BusinessHoursPolicy;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;

@ExtendWith(MockitoExtension.class)
public class TimeOffUseCaseTest {

    @Mock private ProfessionalTimeOffRepository professionalTimeOffRepository;
    @Mock private BusinessHoursPolicy businessHoursPolicy;
    @Mock private ProfessionalRepository professionalRepository;
    @Mock private TimeProvider timeProvider;

    private TimeOffUseCase timeOffUseCase;

    @BeforeEach
    void setUp() {
        timeOffUseCase = new DefaultTimeOffUseCase(professionalTimeOffRepository, businessHoursPolicy, professionalRepository, timeProvider);
    }

    @Test
    public void addValidTimeOff_Success() {
        Long professionalId = 10L;
        OffsetDateTime startAt = OffsetDateTime.parse("2100-01-08T10:00:00Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2100-01-08T12:00:00Z");
      
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", startAt, endAt);
        Professional professional = new Professional("renato", Office.TOSADOR, startAt, endAt);


        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(false);
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));

        AddTimeOffResult result = timeOffUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getProfessionalName()).isEqualTo(professional.getName());
        assertThat(result.getProfessionalId()).isEqualTo(professionalId);
        assertThat(result.getReason()).isEqualTo(command.getReason());

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(timeProvider, times(2)).nowInUTC();
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verify(professionalRepository, times(1)).findById(professionalId);
    }

    @Test
    public void addTimeOff_Fail_DomainValidationException() {
        Long professionalId = null;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", provided, provided);
    
        assertThrows(DomainValidationException.class, () -> {
                timeOffUseCase.execute(command);
            });

        verify(professionalRepository, never()).existsById(anyLong());
        verify(businessHoursPolicy, never()).fits(provided, provided);
        verify(timeProvider, never()).nowInUTC();
        verify(professionalTimeOffRepository, never()).existsOverlap(professionalId, provided, provided);
        verify(professionalRepository, never()).findById(anyLong());
    }

    @Test
    public void addTimeOff_Fail_ExistsById_ProfessionalNotFoundException() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", provided, provided);

        when(professionalRepository.existsById(professionalId)).thenReturn(false);
           
        assertThrows(ProfessionalNotFoundException.class, () -> {
                timeOffUseCase.execute(command);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(businessHoursPolicy, never()).fits(provided, provided);
        verify(timeProvider, never()).nowInUTC();
        verify(professionalTimeOffRepository, never()).existsOverlap(1L, provided, provided);
        verify(professionalRepository, never()).findById(1L);
    }

    @Test
    public void addTimeOff_Fail_WorkingHoursOutsideException() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", provided, provided);

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(businessHoursPolicy.fits(provided, provided)).thenReturn(false);
    
        assertThrows(WorkingHoursOutsideException.class, () -> {
                timeOffUseCase.execute(command);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(businessHoursPolicy, times(1)).fits(provided, provided);
        verify(timeProvider, never()).nowInUTC();
        verify(professionalTimeOffRepository, never()).existsOverlap(professionalId, provided, provided);
        verify(professionalRepository, never()).findById(professionalId);
    }

    @Test
    public void addTimeOff_Fail_ProfessionalTimeOffException() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", provided, provided);

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(businessHoursPolicy.fits(provided, provided)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, provided, provided)).thenReturn(true);
    
        assertThrows(ProfessionalTimeOffException.class, () -> {
                timeOffUseCase.execute(command);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(businessHoursPolicy, times(1)).fits(provided, provided);
        verify(timeProvider, never()).nowInUTC();
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, provided, provided);
        verify(professionalRepository, never()).findById(professionalId);
    }

    @Test
    public void addTimeOff_Fail_FindById_ProfessionalNotFoundException() {
        Long professionalId = 10L;
        OffsetDateTime startAt = OffsetDateTime.parse("2025-12-09T10:00:00Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2025-12-09T12:00:00Z");
        AddTimeOffCommand command = new AddTimeOffCommand(professionalId, "consulta médica", startAt, endAt);

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(businessHoursPolicy.fits(startAt, endAt)).thenReturn(true);
        when(professionalTimeOffRepository.existsOverlap(professionalId, startAt, endAt)).thenReturn(false);
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.empty());
    
        assertThrows(ProfessionalNotFoundException.class, () -> {
                timeOffUseCase.execute(command);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(businessHoursPolicy, times(1)).fits(startAt, endAt);
        verify(timeProvider, times(2)).nowInUTC();
        verify(professionalTimeOffRepository, times(1)).existsOverlap(professionalId, startAt, endAt);
        verify(professionalRepository, times(1)).findById(professionalId);
    }
    
    @Test
    public void delete_Success() {
        Long professionalId = 10L;
        Long timeOffId = 2L;

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(professionalTimeOffRepository.existsById(timeOffId)).thenReturn(true);

        timeOffUseCase.delete(professionalId, timeOffId);

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalTimeOffRepository, times(1)).existsById(timeOffId);
        verify(professionalTimeOffRepository, times(1)).deleteById(timeOffId);

    }

    @Test
    public void delete_ProfessionalNotFound() {
        Long professionalId = 10L;
        Long timeOffId = 2L;

        when(professionalRepository.existsById(professionalId)).thenReturn(false);
        
        assertThrows(ProfessionalNotFoundException.class, () -> {
            timeOffUseCase.delete(professionalId, timeOffId);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalTimeOffRepository, never()).existsById(anyLong());
    }

    @Test
    public void delete_TimeOffNotFound() {
        Long professionalId = 10L;
        Long timeOffId = 2L;

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(professionalTimeOffRepository.existsById(timeOffId)).thenReturn(false);
    
        assertThrows(ProfessionalTimeOffException.class, () -> {
            timeOffUseCase.delete(professionalId, timeOffId);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalTimeOffRepository, times(1)).existsById(anyLong());
    }
}
