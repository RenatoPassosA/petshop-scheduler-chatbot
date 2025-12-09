package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public class ProfessionalTimeOffTest {

    @Test
    public void CreateProfessionalTimeOff_Success() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        ProfessionalTimeOff professionalTimeOff = new ProfessionalTimeOff(timeOffId, professionalId, "consulta medica", startAt, endAt, date, date);

        assertThat(professionalTimeOff).isNotNull();
        assertThat(professionalTimeOff.getId()).isEqualTo(2L);
        assertThat(professionalTimeOff.getProfessionalId()).isEqualTo(1L);
        assertThat(professionalTimeOff.getReason()).isEqualTo("consulta medica");
        assertThat(professionalTimeOff.getStartAt()).isEqualTo(startAt);
        assertThat(professionalTimeOff.getEndAt()).isEqualTo(endAt);
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_ReasonIsNull() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, null, startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Success_ReasonTrimmed() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        ProfessionalTimeOff professionalTimeOff = new ProfessionalTimeOff(timeOffId, professionalId, "    consulta medica    ", startAt, endAt, date, date);

        assertThat(professionalTimeOff).isNotNull();
        assertThat(professionalTimeOff.getId()).isEqualTo(2L);
        assertThat(professionalTimeOff.getProfessionalId()).isEqualTo(1L);
        assertThat(professionalTimeOff.getReason()).isEqualTo("consulta medica");
        assertThat(professionalTimeOff.getStartAt()).isEqualTo(startAt);
        assertThat(professionalTimeOff.getEndAt()).isEqualTo(endAt);
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_ReasonIsSpace() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "      ", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EmptyReason() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullProfessionalId() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, null, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Profissional inválido");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NegativeProfessionalId() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.plusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = -1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Profissional inválido");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullStartAt() {
        OffsetDateTime startAt = null;
        OffsetDateTime endAt = OffsetDateTime.now();
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullEndAt() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = null;
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("endAt é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EndAtIsBeforeStartAt() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endAt = startAt.minusMinutes(90);
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt deve ser antes de endAt");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EndAtIsEqualStartAt() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-20T10:00:00Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2025-01-20T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt deve ser antes de endAt");
    }
}
