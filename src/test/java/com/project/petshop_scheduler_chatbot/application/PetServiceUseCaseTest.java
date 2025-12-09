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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.exceptions.PetServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceResult;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.impl.DefaultRegisterPetServiceUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;

@ExtendWith(MockitoExtension.class)
public class PetServiceUseCaseTest {

    @Mock
    private PetServiceRepository petServiceRepository;

    @Mock
    private TimeProvider timeProvider;

    private PetServiceUseCase petServiceUseCase;

    @BeforeEach
    void setUp() {
        petServiceUseCase = new DefaultRegisterPetServiceUseCase(petServiceRepository, timeProvider);
    }

    @Test
    public void addValidPetService() {
        Long petServiceId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddPetServiceCommand command = new AddPetServiceCommand("tosa", new BigDecimal(100), 180);
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        PetService petServiceWithId = petService.withPersistenceId(petServiceId);

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(petServiceRepository.save(any(PetService.class))).thenReturn(petServiceWithId);
        when(petServiceRepository.findByName(command.getName())).thenReturn(new ArrayList<>());


        AddPetServiceResult result = petServiceUseCase.register(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(petServiceId);
        assertThat(result.getName()).isEqualTo(command.getName());
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.getDuration()).isEqualTo(command.getDuration());

        verify(petServiceRepository, times(1)).save(any(PetService.class));
        verify(timeProvider, times(2)).nowInUTC();
    }

    @Test
    public void addPetService_Fail_DomainValidationException() {
                AddPetServiceCommand command = new AddPetServiceCommand("tosa", new BigDecimal(100), 10);

    
        assertThrows(DomainValidationException.class, () -> {
                petServiceUseCase.register(command);
            });

        verify(petServiceRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
    }

    @Test
    public void addPetService_Fail_DomainValidationException_NotMultiple15() {
                AddPetServiceCommand command = new AddPetServiceCommand("tosa", new BigDecimal(100), 155);

    
        assertThrows(DomainValidationException.class, () -> {
                petServiceUseCase.register(command);
            });

        verify(petServiceRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
    }

    @Test
    public void addPetService_Fail_DuplicatedName() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddPetServiceCommand command = new AddPetServiceCommand("tosa", new BigDecimal(100), 180);

        List<PetService> existing = List.of(new PetService("tosa", new BigDecimal(80), 120, provided, provided));

        when(petServiceRepository.findByName(command.getName())).thenReturn(existing);

        assertThrows(InvalidAppointmentStateException.class, () -> {
            petServiceUseCase.register(command);
        });

        verify(petServiceRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
    }

    @Test
    public void getPetService_Sucess() {
        Long petServiceId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PetService expectedPetService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        PetService expectedPetServiceWithId = expectedPetService.withPersistenceId(petServiceId);

        when(petServiceRepository.findById(petServiceId)).thenReturn(Optional.of(expectedPetServiceWithId));

        PetService getPetService = petServiceUseCase.getPetService(petServiceId);

        assertThat(getPetService).isEqualTo(expectedPetServiceWithId);
        assertThat(getPetService.getId()).isEqualTo(petServiceId);
        assertThat(getPetService.getName()).isEqualTo(expectedPetServiceWithId.getName());
        assertThat(getPetService.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(getPetService.getDuration()).isEqualTo(expectedPetServiceWithId.getDuration());

        verify(petServiceRepository, times(1)).findById(petServiceId);
    }

    @Test
    public void getPetService_NotFound() {
        Long petServiceId = 10L;

        when(petServiceRepository.findById(petServiceId)).thenReturn(Optional.empty());
    
        assertThrows(PetServiceNotFoundException.class, () -> {
            petServiceUseCase.getPetService(petServiceId);
            });

        verify(petServiceRepository, times(1)).findById(petServiceId);
    }

    @Test
    public void getAll_PopulatedList() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        List<PetService> petServiceList = new ArrayList<>();
        petServiceList.add(new PetService("tosa", new BigDecimal(80), 120, provided, provided));
        petServiceList.add(new PetService("banho", new BigDecimal(60), 120, provided, provided));
        petServiceList.add(new PetService("banho e tosa", new BigDecimal(120), 180, provided, provided));

        when(petServiceRepository.getAll()).thenReturn(petServiceList);

        List<PetService> result = petServiceUseCase.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(petServiceList);

        verify(petServiceRepository, times(1)).getAll();
    }

    @Test
    public void getAll_EmptyList() {
        List<PetService> petServiceList = new ArrayList<>();

        when(petServiceRepository.getAll()).thenReturn(petServiceList);

        List<PetService> result = petServiceUseCase.getAll();

        assertThat(result).isEmpty();

        verify(petServiceRepository, times(1)).getAll();
    }


    @Test
    public void update_Success() {
        Long petServiceId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PetService expectedPetService = new PetService("tosa", new BigDecimal(100), 180, provided, provided);
        PetService expectedPetServiceWithId = expectedPetService.withPersistenceId(petServiceId);
        UpdatePetServiceCommand command = new UpdatePetServiceCommand(new BigDecimal(200), 200);

        when(petServiceRepository.findById(petServiceId)).thenReturn(Optional.of(expectedPetServiceWithId));

        petServiceUseCase.update(petServiceId, command);

        assertThat(expectedPetServiceWithId.getName()).isEqualTo("tosa");
        assertThat(expectedPetServiceWithId.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(expectedPetServiceWithId.getDuration()).isEqualTo(200);

        verify(petServiceRepository, times(1)).findById(petServiceId);
        verify(petServiceRepository, times(1)).save(expectedPetServiceWithId);
    }

    @Test
    public void update_PartialUpdate_Success() {
        Long petServiceId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PetService expectedPetService = new PetService("tosa", new BigDecimal(100), 150, provided, provided);
        PetService expectedPetServiceWithId = expectedPetService.withPersistenceId(petServiceId);
        UpdatePetServiceCommand command = new UpdatePetServiceCommand(null, 180);

        when(petServiceRepository.findById(petServiceId)).thenReturn(Optional.of(expectedPetServiceWithId));

        petServiceUseCase.update(petServiceId, command);
    
        assertThat(expectedPetServiceWithId.getName()).isEqualTo("tosa");
        assertThat(expectedPetServiceWithId.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(expectedPetServiceWithId.getDuration()).isEqualTo(180);

        verify(petServiceRepository, times(1)).findById(petServiceId);
        verify(petServiceRepository, times(1)).save(expectedPetServiceWithId);
    }

    @Test
    public void update_PetServiceNotFound() {
        Long petServiceId = 10L;
        UpdatePetServiceCommand command = new UpdatePetServiceCommand(new BigDecimal(200), 200);
        
        when(petServiceRepository.findById(petServiceId)).thenReturn(Optional.empty());

        assertThrows(PetServiceNotFoundException.class, () -> {
            petServiceUseCase.update(petServiceId, command);
            });

        verify(petServiceRepository, times(1)).findById(petServiceId);
        verify(petServiceRepository, never()).save(any(PetService.class));
    }

    @Test
    public void delete_Success() {
        Long petServiceId = 10L;

        when(petServiceRepository.existsById(petServiceId)).thenReturn(true);

        petServiceUseCase.delete(petServiceId);

        verify(petServiceRepository, times(1)).existsById(petServiceId);
        verify(petServiceRepository, times(1)).deleteById(petServiceId);
    }

    @Test
    public void delete_PetServiceNotFound() {
        Long petServiceId = 10L;

        when(petServiceRepository.existsById(petServiceId)).thenReturn(false);
    
        assertThrows(PetServiceNotFoundException.class, () -> {
            petServiceUseCase.delete(petServiceId);
            });

        verify(petServiceRepository, times(1)).existsById(petServiceId);
        verify(petServiceRepository, never()).deleteById(anyLong());
    }  
}
