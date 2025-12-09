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

import com.project.petshop_scheduler_chatbot.application.exceptions.PetNotFoundException;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorResult;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.application.pet.UpdatePetCommand;
import com.project.petshop_scheduler_chatbot.application.pet.impl.DefaultPetUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
public class PetUseCaseTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TimeProvider timeProvider;

    private PetUseCase petUseCase;

    @BeforeEach
    void setUp() {
        petUseCase = new DefaultPetUseCase(petRepository, tutorRepository, timeProvider);

    }

    @Test
    public void addValidPetToTutor() {
        Long petId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        AddPetToTutorCommand command = new AddPetToTutorCommand("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok");
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok", provided, provided);
        Pet petWithId = pet.withPersistenceId(petId);

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsById(command.getTutorId())).thenReturn(true);
        when(petRepository.save(any(Pet.class))).thenReturn(petWithId);

        AddPetToTutorResult result = petUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getPetId()).isEqualTo(petId);
        assertThat(result.getTutorId()).isEqualTo(command.getTutorId());
        assertThat(result.getPetName()).isEqualTo(command.getName());
        assertThat(result.getObservation()).isEqualTo(command.getObservation());

        verify(petRepository, times(1)).save(any(Pet.class));
        verify(timeProvider, times(2)).nowInUTC();
        verify(tutorRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void addPetToTutor_Fail_DomainValidationException() {
        AddPetToTutorCommand command = new AddPetToTutorCommand(null, Gender.F, PetSize.SMALL, "york", 1L, "ok");
    
        assertThrows(DomainValidationException.class, () -> {
                petUseCase.execute(command);
            });

        verify(petRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
        verify(tutorRepository, never()).existsById(anyLong());
    }

    @Test
    public void addPetToTutor_Fail_TutorNotExist() {
        AddPetToTutorCommand command = new AddPetToTutorCommand("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok");

        when(tutorRepository.existsById(command.getTutorId())).thenReturn(false);

        assertThrows(DomainValidationException.class, () -> {
            petUseCase.execute(command);
        });

        verify(petRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
        verify(tutorRepository, times(1)).existsById(command.getTutorId());

    }

    @Test
    public void getPet_Sucess() {
        Long petId = 10L;
        Pet expectedPet = new Pet("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok", OffsetDateTime.now(), OffsetDateTime.now());
        Pet expectedPetWithId = expectedPet.withPersistenceId(petId);

        when(petRepository.findById(petId)).thenReturn(Optional.of(expectedPetWithId));

        Pet getPet = petUseCase.getPet(petId);

        assertThat(getPet).isEqualTo(expectedPetWithId);
        assertThat(getPet.getId()).isEqualTo(petId);
        assertThat(getPet.getName()).isEqualTo(expectedPetWithId.getName());
        assertThat(getPet.getGender()).isEqualTo(expectedPetWithId.getGender());
        assertThat(getPet.getSize()).isEqualTo(expectedPetWithId.getSize());
        assertThat(getPet.getBreed()).isEqualTo(expectedPetWithId.getBreed());
        assertThat(getPet.getTutorId()).isEqualTo(expectedPetWithId.getTutorId());
        assertThat(getPet.getObservations()).isEqualTo(expectedPetWithId.getObservations());
       
        verify(petRepository, times(1)).findById(petId);
    }

    @Test
    public void getPet_NotFound() {
        Long petId = 10L;

        when(petRepository.findById(petId)).thenReturn(Optional.empty());
    
        assertThrows(PetNotFoundException.class, () -> {
            petUseCase.getPet(petId);
            });

        verify(petRepository, times(1)).findById(petId);
    }

    @Test
    public void getAll_PopulatedList() {
        List<Pet> petList = new ArrayList<>();
        petList.add(new Pet("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok", OffsetDateTime.now(), OffsetDateTime.now()));
        petList.add(new Pet("kiwi", Gender.M, PetSize.MEDIUM, "shitzu", 1L, "ok", OffsetDateTime.now(), OffsetDateTime.now()));
        petList.add(new Pet("manu", Gender.F, PetSize.LARGE, "dalmata", 2L, "ok", OffsetDateTime.now(), OffsetDateTime.now()));
        petList.add(new Pet("luke", Gender.M, PetSize.SMALL, "york", 2L, "ok", OffsetDateTime.now(), OffsetDateTime.now()));
        petList.add(new Pet("zeus", Gender.M, PetSize.LARGE, "dalmata", 2L, "ok", OffsetDateTime.now(), OffsetDateTime.now()));

        when(petRepository.getAll()).thenReturn(petList);

        List<Pet> result = petUseCase.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(5);
        assertThat(result).containsExactlyElementsOf(petList);

        verify(petRepository, times(1)).getAll();
    }

    @Test
    public void getAll_EmptyList() {
        List<Pet> petServiceList = new ArrayList<>();

        when(petRepository.getAll()).thenReturn(petServiceList);

        List<Pet> result = petUseCase.getAll();

        assertThat(result).isEmpty();

        verify(petRepository, times(1)).getAll();
    }

    @Test
    public void update_Success() {
        Long petId = 10L;
        Long tutorId = 2L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Pet expectedPet = new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, null, provided, provided);
        Pet expectedPetWithId = expectedPet.withPersistenceId(petId);
        UpdatePetCommand command = new UpdatePetCommand(petId, "pós operado");

        when(petRepository.findById(petId)).thenReturn(Optional.of(expectedPetWithId));

        petUseCase.update(petId, command);
    
        assertThat(expectedPetWithId.getName()).isEqualTo("flor");
        assertThat(expectedPetWithId.getGender()).isEqualTo(Gender.F);
        assertThat(expectedPetWithId.getSize()).isEqualTo(PetSize.SMALL);
        assertThat(expectedPetWithId.getBreed()).isEqualTo("york");
        assertThat(expectedPetWithId.getTutorId()).isEqualTo(tutorId);
        assertThat(expectedPetWithId.getObservations()).isEqualTo("pós operado");
           
        verify(petRepository, times(1)).findById(petId);
        verify(petRepository, times(1)).save(expectedPetWithId);
    }

    @Test
    public void update_PetNotFound() {
        Long petId = 10L;
        UpdatePetCommand command = new UpdatePetCommand(10L, "orelha inflamada");
        
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(PetNotFoundException.class, () -> {
            petUseCase.update(petId, command);
            });

        verify(petRepository, times(1)).findById(petId);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    public void delete_Success() {
        Long petId = 10L;

        when(petRepository.existsById(petId)).thenReturn(true);

        petUseCase.delete(petId);

        verify(petRepository, times(1)).existsById(petId);
        verify(petRepository, times(1)).deleteById(petId);
    }

    @Test
    public void delete_PetNotFound() {
        Long petId = 10L;

        when(petRepository.existsById(petId)).thenReturn(false);
    
        assertThrows(PetNotFoundException.class, () -> {
            petUseCase.delete(petId);
            });

        verify(petRepository, times(1)).existsById(petId);
        verify(petRepository, never()).deleteById(anyLong());
    }  
}

