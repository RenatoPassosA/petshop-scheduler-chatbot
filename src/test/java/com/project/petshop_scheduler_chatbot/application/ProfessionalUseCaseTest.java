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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalResult;
import com.project.petshop_scheduler_chatbot.application.professional.ProfessionalUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.impl.DefaultProfessionalUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;

@ExtendWith(MockitoExtension.class)
public class ProfessionalUseCaseTest {

    @Mock private ProfessionalRepository professionalRepository;
    @Mock private TimeProvider timeProvider;

    private ProfessionalUseCase professionalUseCase;

    @BeforeEach
    void setUp() {
        professionalUseCase = new DefaultProfessionalUseCase(professionalRepository, timeProvider);
    }

    @Test
    public void addValidProfessional_Success() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddProfessionalCommand command = new AddProfessionalCommand("Renato", Office.VET);
        Professional professional = new Professional("Renato", Office.VET, provided, provided);
        Professional professionalWithId = professional.withPersistenceId(professionalId);

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(professionalRepository.save(any(Professional.class))).thenReturn(professionalWithId);

        AddProfessionalResult result = professionalUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getProfessionalId()).isEqualTo(professionalId);
        assertThat(result.getName()).isEqualTo(command.getName());
        assertThat(result.getFunction()).isEqualTo(command.getFunction());

        verify(professionalRepository, times(1)).save(any(Professional.class));
        verify(timeProvider, times(2)).nowInUTC();
    }

    @Test
    public void addProfessional_Fail_DomainValidationException() {
        AddProfessionalCommand command = new AddProfessionalCommand(null, Office.VET);
    
        assertThrows(DomainValidationException.class, () -> {
                professionalUseCase.execute(command);
            });

        verify(professionalRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
    }


    @Test
    public void getProfessional_Sucess() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Professional expectedProfessional = new Professional("renato", Office.AUX, provided, provided);
        Professional expectedProfessionalWithId = expectedProfessional.withPersistenceId(professionalId);

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(expectedProfessionalWithId));

        Professional getTutor = professionalUseCase.getProfessional(professionalId);

        assertThat(getTutor).isEqualTo(expectedProfessionalWithId);
        assertThat(getTutor.getId()).isEqualTo(professionalId);
        assertThat(getTutor.getName()).isEqualTo(expectedProfessionalWithId.getName());

        verify(professionalRepository, times(1)).findById(professionalId);
    }

    @Test
    public void getProfessional_NotFound() {
        Long professionalId = 10L;

        when(professionalRepository.findById(professionalId)).thenReturn(Optional.empty());
    
        assertThrows(ProfessionalNotFoundException.class, () -> {
            professionalUseCase.getProfessional(professionalId);
            });

        verify(professionalRepository, times(1)).findById(professionalId);
    }

    @Test
    public void getAll_PopulatedList() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        List<Professional> professionalList = new ArrayList<>();
        professionalList.add(new Professional("renato", Office.AUX, provided, provided));
        professionalList.add(new Professional("amanda", Office.TOSADOR, provided, provided));
        professionalList.add(new Professional("lucas", Office.VET, provided, provided));

        when(professionalRepository.getAll()).thenReturn(professionalList);

        List<Professional> result = professionalUseCase.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(professionalList);

        verify(professionalRepository, times(1)).getAll();
    }

    @Test
    public void getAll_EmptyList() {
        List<Professional> professionalList = new ArrayList<>();

        when(professionalRepository.getAll()).thenReturn(professionalList);

        List<Professional> result = professionalUseCase.getAll();

        assertThat(result).isEmpty();

        verify(professionalRepository, times(1)).getAll();
    }


    @Test
    public void update_Success() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Professional professional = new Professional("renato", Office.AUX, provided, provided);
        Professional professionalWithId = professional.withPersistenceId(professionalId);
        UpdateProfessionalCommand command = new UpdateProfessionalCommand("amanda", Office.VET);

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professionalWithId));

        professionalUseCase.update(professionalId, command);

        assertThat(professionalWithId.getName()).isEqualTo("amanda");
        assertThat(professionalWithId.getFunction()).isEqualTo(Office.VET);

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalRepository, times(1)).findById(professionalId);
        verify(professionalRepository, times(1)).save(professionalWithId);
    }

    @Test
    public void update_PartialUpdate_Success() {
        Long professionalId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Professional professional = new Professional("ren", Office.AUX, provided, provided);
        Professional professionalWithId = professional.withPersistenceId(professionalId);
        UpdateProfessionalCommand command = new UpdateProfessionalCommand("renato", null);

        when(professionalRepository.existsById(professionalId)).thenReturn(true);
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professionalWithId));

        professionalUseCase.update(professionalId, command);
    
        assertThat(professionalWithId.getName()).isEqualTo("renato");
        assertThat(professionalWithId.getFunction()).isEqualTo(Office.AUX);

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalRepository, times(1)).findById(professionalId);
        verify(professionalRepository, times(1)).save(professionalWithId);
    }

    @Test
    public void update_ProfessionalNotFound() {
        Long professionalId = 10L;
        UpdateProfessionalCommand command = new UpdateProfessionalCommand("renato", null);


        when(professionalRepository.existsById(professionalId)).thenReturn(false);

        assertThrows(ProfessionalNotFoundException.class, () -> {
            professionalUseCase.update(professionalId, command);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalRepository, never()).findById(anyLong());
        verify(professionalRepository, never()).save(any(Professional.class));
    }

    @Test
    public void delete_Success() {
        Long professionalId = 10L;

        when(professionalRepository.existsById(professionalId)).thenReturn(true);

        professionalUseCase.delete(professionalId);

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalRepository, times(1)).deleteById(professionalId);
    }

    @Test
    public void delete_ProfessionalNotFound() {
        Long professionalId = 10L;

        when(professionalRepository.existsById(professionalId)).thenReturn(false);
    
        assertThrows(ProfessionalNotFoundException.class, () -> {
            professionalUseCase.delete(professionalId);
            });

        verify(professionalRepository, times(1)).existsById(professionalId);
        verify(professionalRepository, never()).deleteById(anyLong());
    }

    
}
