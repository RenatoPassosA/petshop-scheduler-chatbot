package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class TutorTest {

    @Test
    public void CreateTutor_Success() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("renato", phoneNumber, "rua 1", date, date);

        assertThat(tutor).isNotNull();
        assertThat(tutor.getId()).isNull();
        assertThat(tutor.getName()).isEqualTo("renato");
        assertThat(tutor.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(tutor.getAddress()).isEqualTo("rua 1");
        assertThat(tutor.getCreatedAt()).isEqualTo(date);
        assertThat(tutor.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateTutor_Success_NameAndAddressTrimmed() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("  renato", phoneNumber, "   rua 1      ", date, date);

        assertThat(tutor).isNotNull();
        assertThat(tutor.getId()).isNull();
        assertThat(tutor.getName()).isEqualTo("renato");
        assertThat(tutor.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(tutor.getAddress()).isEqualTo("rua 1");
        assertThat(tutor.getCreatedAt()).isEqualTo(date);
        assertThat(tutor.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateTutor_Fail_NameIsSpace() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Tutor("   ", phoneNumber, "rua 1", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Tutor é obrigatório");
    }

    @Test
    public void CreateTutor_Fail_EmptyName() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Tutor("", phoneNumber, "rua 1", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Tutor é obrigatório");
    }

    @Test
    public void CreateTutor_Fail_NullName() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Tutor(null, phoneNumber, "rua 1", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Tutor é obrigatório");
    }

    @Test
    public void CreateTutor_Fail_NullPhone() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Tutor("renato", null, "rua 1", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Telefone do Tutor é obrigatório");
    }

    @Test
    public void CreateTutor_Fail_NullAddress() {
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Tutor("renato", phoneNumber, null, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Endereço do Tutor é obrigatório");
    }

    @Test
    public void CreateTutorWithId_Success() {
        Long tutorId = 10L;
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("  renato", phoneNumber, "   rua 1      ", date, date);

        Tutor tutorWithId = tutor.withPersistenceId(tutorId);

        assertThat(tutorWithId).isNotNull();
        assertThat(tutorWithId.getId()).isEqualTo(10L);
        assertThat(tutorWithId.getName()).isEqualTo("renato");
        assertThat(tutorWithId.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(tutorWithId.getAddress()).isEqualTo("rua 1");
        assertThat(tutorWithId.getCreatedAt()).isEqualTo(date);
        assertThat(tutorWithId.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateTutorWithId_Fail_NullId() {
        Long tutorId = null;
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("  renato", phoneNumber, "   rua 1      ", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            tutor.withPersistenceId(tutorId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreateTutorWithId_Fail_NegativeId() {
        Long tutorId = -3L;
        PhoneNumber phoneNumber = new PhoneNumber("123456789");
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("  renato", phoneNumber, "   rua 1      ", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            tutor.withPersistenceId(tutorId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }
}
