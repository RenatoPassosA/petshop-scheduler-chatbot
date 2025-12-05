package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class ProfessionalTest {

    @Test
    public void CreateProfessional_Success() {
        OffsetDateTime date = OffsetDateTime.now();
        Professional professional = new Professional("renato", Office.AUX, date, date);

        assertThat(professional).isNotNull();
        assertThat(professional.getId()).isNull();
        assertThat(professional.getName()).isEqualTo("renato");
        assertThat(professional.getFunction()).isEqualTo(Office.AUX);
        assertThat(professional.getCreatedAt()).isEqualTo(date);
        assertThat(professional.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateProfessional_Success_NameTrimmed() {
        OffsetDateTime date = OffsetDateTime.now();
        Professional professional = new Professional("   renato   ", Office.AUX, date, date);

        assertThat(professional).isNotNull();
        assertThat(professional.getId()).isNull();
        assertThat(professional.getName()).isEqualTo("renato");
        assertThat(professional.getFunction()).isEqualTo(Office.AUX);
        assertThat(professional.getCreatedAt()).isEqualTo(date);
        assertThat(professional.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateProfessional_Fail_NameIsSpace() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Professional("   ", Office.AUX, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Profissional é obrigatório");
    }

    @Test
    public void CreateProfessional_Fail_EmptyName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Professional("", Office.AUX, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Profissional é obrigatório");
    }

    @Test
    public void CreateProfessional_Fail_NullName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Professional(null, Office.AUX, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Profissional é obrigatório");
    }

    @Test
    public void CreateProfessional_Fail_NullOffice() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Professional("renato", null, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Função é obrigatória");
    }

    @Test
    public void CreateProfessionalWithId_Success() {
        Long professionalId = 10L;
        OffsetDateTime date = OffsetDateTime.now();
        Professional professional = new Professional("  renato", Office.AUX, date, date);

        Professional professionalWithId = professional.withPersistenceId(professionalId);

        assertThat(professionalWithId).isNotNull();
        assertThat(professionalWithId.getId()).isEqualTo(10L);
        assertThat(professionalWithId.getName()).isEqualTo("renato");
        assertThat(professionalWithId.getFunction()).isEqualTo(Office.AUX);
        assertThat(professionalWithId.getCreatedAt()).isEqualTo(date);
        assertThat(professionalWithId.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateProfessionalWithId_Fail_NullId() {
        Long professionalId = null;
        OffsetDateTime date = OffsetDateTime.now();
        Professional professional = new Professional("renato", Office.AUX, date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            professional.withPersistenceId(professionalId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreateProfessionalWithId_Fail_NegativeId() {
        Long professionalId = -3L;
        OffsetDateTime date = OffsetDateTime.now();
        Professional professional = new Professional("renato", Office.AUX, date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            professional.withPersistenceId(professionalId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }
}
