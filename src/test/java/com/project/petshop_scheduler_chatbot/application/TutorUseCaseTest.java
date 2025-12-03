package com.project.petshop_scheduler_chatbot.application;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.petshop_scheduler_chatbot.application.exceptions.DuplicatedPhoneNumberException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.application.tutor.UpdateTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.impl.DefaultTutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.application.TimeProvider;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
public class TutorUseCaseTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private TimeProvider timeProvider;

    private TutorUseCase tutorUseCase;

    @BeforeEach
    void setUp() {
        tutorUseCase = new DefaultTutorUseCase(tutorRepository, timeProvider);
    }

    @Test
    public void addValidTutor_Success() {
        Long tutorId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PhoneNumber number = new PhoneNumber("123456789");
        AddTutorCommand command = new AddTutorCommand("renato", number, "rua 1");
        Tutor tutor = new Tutor("renato", number, "rua 1", provided, provided);
        Tutor tutorWithId = tutor.withPersistenceId(tutorId);

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsByPhone(command.getPhoneNumber())).thenReturn(false);
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutorWithId);

        AddTutorResult result = tutorUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getTutorId()).isEqualTo(tutorId);
        assertThat(result.getName()).isEqualTo(command.getName());
        assertThat(result.getPhoneNumber()).isEqualTo(command.getPhoneNumber().value());
        assertThat(result.getAddress()).isEqualTo(command.getAddress());

        verify(tutorRepository, times(1)).save(any(Tutor.class));
        verify(tutorRepository, times(1)).existsByPhone(any(PhoneNumber.class));
        verify(timeProvider, times(2)).nowInUTC();
    }

    @Test
    public void addTutor_Fail_DuplicatedPhone() {
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PhoneNumber number = new PhoneNumber("123456789");
        AddTutorCommand command = new AddTutorCommand("renato", number, "rua 1");

        when(timeProvider.nowInUTC()).thenReturn(provided);
        when(tutorRepository.existsByPhone(command.getPhoneNumber())).thenReturn(true);

        assertThrows(DuplicatedPhoneNumberException.class, () -> {
                tutorUseCase.execute(command);
            });

        verify(timeProvider, times(2)).nowInUTC();
        verify(tutorRepository, never()).save(any(Tutor.class));
    }

    @Test
    public void addTutor_Fail_DomainValidationException() {
        AddTutorCommand command = new AddTutorCommand("renato", null, "rua 1");
    
        assertThrows(DomainValidationException.class, () -> {
                tutorUseCase.execute(command);
            });

        verify(tutorRepository, never()).existsByPhone(any());
        verify(tutorRepository, never()).save(any());
        verify(timeProvider, never()).nowInUTC();
    }


    @Test
    public void getTutor_Sucess() {
        Long tutorId = 10L;
        PhoneNumber number = new PhoneNumber("123456789");
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Tutor expectedTutor = new Tutor("renato", number, "rua 1", provided, provided);
        Tutor expectedTutorWithId = expectedTutor.withPersistenceId(tutorId);

        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(expectedTutorWithId));

        Tutor getTutor = tutorUseCase.getTutor(tutorId);

        assertThat(getTutor).isEqualTo(expectedTutorWithId);
        assertThat(getTutor.getId()).isEqualTo(tutorId);
        assertThat(getTutor.getName()).isEqualTo(expectedTutorWithId.getName());
        assertThat(getTutor.getPhoneNumber()).isEqualTo(expectedTutorWithId.getPhoneNumber());
        assertThat(getTutor.getAddress()).isEqualTo(expectedTutorWithId.getAddress());

        verify(tutorRepository, times(1)).findById(tutorId);
    }

    @Test
    public void getTutor_NotFound() {
        Long tutorId = 10L;

        when(tutorRepository.findById(tutorId)).thenReturn(Optional.empty());
    
        assertThrows(TutorNotFoundException.class, () -> {
            tutorUseCase.getTutor(tutorId);
            });

        verify(tutorRepository, times(1)).findById(tutorId);

        /*Não se deve mockar assim: when(tutorRepository.findById(tutorId)).thenThrow(...)
        Porque no fluxo real findById não lança exceção. Ele retorna Optional.empty()
        É o use case que deve lançar TutorNotFoundException
        */
    }

    @Test
    public void getAll_PopulatedList() {
        PhoneNumber number = new PhoneNumber("123456789");
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        List<Tutor> tutorList = new ArrayList<>();
        tutorList.add(new Tutor("renato", number, "rua 1", provided, provided));
        tutorList.add(new Tutor("amanda", number, "rua 2", provided, provided));
        tutorList.add(new Tutor("lucas", number, "rua 3", provided, provided));

        when(tutorRepository.getAll()).thenReturn(tutorList);

        List<Tutor> result = tutorUseCase.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(tutorList);

        verify(tutorRepository, times(1)).getAll();
    }

    @Test
    public void getAll_EmptyList() {
        List<Tutor> tutorList = new ArrayList<>();

        when(tutorRepository.getAll()).thenReturn(tutorList);

        List<Tutor> result = tutorUseCase.getAll();

        assertThat(result).isEmpty();

        verify(tutorRepository, times(1)).getAll();
    }

    @Test
    public void update_Success() {
        Long tutorId = 10L;
        PhoneNumber number = new PhoneNumber("123456789");
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        Tutor tutor = new Tutor("renato", number, "rua 1", provided, provided);
        Tutor tutorWithId = tutor.withPersistenceId(tutorId);
        PhoneNumber newPhoneNumber = new PhoneNumber("111156789");
        UpdateTutorCommand command = new UpdateTutorCommand("amanda", newPhoneNumber, "ruaa 2");

        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutorWithId));

        tutorUseCase.update(tutorId, command);

        assertThat(tutorWithId.getName()).isEqualTo("amanda");
        assertThat(tutorWithId.getPhoneNumber()).isEqualTo(newPhoneNumber);
        assertThat(tutorWithId.getAddress()).isEqualTo("ruaa 2");

        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(tutorRepository, times(1)).findById(tutorId);
        verify(tutorRepository, times(1)).save(tutorWithId);
    }

    @Test
    public void update_PartialUpdate_Success() {
        Long tutorId = 10L;
        OffsetDateTime provided = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        PhoneNumber originalPhone = new PhoneNumber("123456789");
        Tutor tutor = new Tutor("renato", originalPhone, "rua 1", provided, provided);
        Tutor tutorWithId = tutor.withPersistenceId(tutorId);
        PhoneNumber newPhone = new PhoneNumber("987654321");
        UpdateTutorCommand command = new UpdateTutorCommand(null, newPhone, null);

        when(tutorRepository.existsById(tutorId)).thenReturn(true);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutorWithId));

        tutorUseCase.update(tutorId, command);
    
        assertThat(tutorWithId.getName()).isEqualTo("renato");
        assertThat(tutorWithId.getAddress()).isEqualTo("rua 1");
    
        assertThat(tutorWithId.getPhoneNumber()).isEqualTo(newPhone);

        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(tutorRepository, times(1)).findById(tutorId);
        verify(tutorRepository, times(1)).save(tutorWithId);
    }

    @Test
    public void update_TutorNotFound() {
        Long tutorId = 10L;
        PhoneNumber number = new PhoneNumber("123456789");
        UpdateTutorCommand command = new UpdateTutorCommand("amanda", number, "rua 2");

        when(tutorRepository.existsById(tutorId)).thenReturn(false);

        assertThrows(TutorNotFoundException.class, () -> {
            tutorUseCase.update(tutorId, command);
            });

        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(tutorRepository, never()).findById(anyLong());
        verify(tutorRepository, never()).save(any(Tutor.class));
    }

    @Test
    public void delete_Success() {
        Long tutorId = 10L;

        when(tutorRepository.existsById(tutorId)).thenReturn(true);

        tutorUseCase.delete(tutorId);

        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(tutorRepository, times(1)).deleteById(tutorId);
    }

    @Test
    public void delete_TutorNotFound() {
        Long tutorId = 10L;

        when(tutorRepository.existsById(tutorId)).thenReturn(false);
    
        assertThrows(TutorNotFoundException.class, () -> {
            tutorUseCase.delete(tutorId);
            });

        verify(tutorRepository, times(1)).existsById(tutorId);
        verify(tutorRepository, never()).deleteById(anyLong());
    }
}


/*
Regra geral
Sempre que você depender do retorno de um método mockado para continuar o fluxo do use case, você PRECISA configurar o mock.
Se o seu código usa o valor retornado por um método você precisa dizer ao mock o que retornar.

Quando você NÃO precisa configurar o mock:
Quando o método é void ou quando o método não é usado no fluxo que você está testando.
*eu PRECISO configurar um método void quando eu quero que ele lance uma exceção.

*/