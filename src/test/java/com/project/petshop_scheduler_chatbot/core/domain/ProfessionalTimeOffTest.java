package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public class ProfessionalTimeOffTest {

    @Test
    public void CreateProfessionalTimeOff_Success() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        ProfessionalTimeOff professionalTimeOff = new ProfessionalTimeOff(timeOffId, professionalId, "consulta medica", startAt, endAt);

        assertThat(professionalTimeOff).isNotNull();
        assertThat(professionalTimeOff.getId()).isEqualTo(2L);
        assertThat(professionalTimeOff.getProfessionalId()).isEqualTo(1L);
        assertThat(professionalTimeOff.getReason()).isEqualTo("consulta medica");
        assertThat(professionalTimeOff.getStartAt()).isEqualTo(startAt);
        assertThat(professionalTimeOff.getEndAt()).isEqualTo(endAt);
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_ReasonIsNull() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, null, startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Success_ReasonTrimmed() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        ProfessionalTimeOff professionalTimeOff = new ProfessionalTimeOff(timeOffId, professionalId, "    consulta medica    ", startAt, endAt);

        assertThat(professionalTimeOff).isNotNull();
        assertThat(professionalTimeOff.getId()).isEqualTo(2L);
        assertThat(professionalTimeOff.getProfessionalId()).isEqualTo(1L);
        assertThat(professionalTimeOff.getReason()).isEqualTo("consulta medica");
        assertThat(professionalTimeOff.getStartAt()).isEqualTo(startAt);
        assertThat(professionalTimeOff.getEndAt()).isEqualTo(endAt);
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_ReasonIsSpace() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "      ", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EmptyReason() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("Motivo é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullProfessionalId() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, null, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("Profissional inválido");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NegativeProfessionalId() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.plusMinutes(90);
        Long professionalId = -1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("Profissional inválido");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullStartAt() {
        OffsetDateTime startAt = null;
        OffsetDateTime endAt = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_NullEndAt() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = null;
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("endAt é obrigatório");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EndAtIsBeforeStartAt() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = startAt.minusMinutes(90);
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt deve ser antes de endAt");
    }

    @Test
    public void CreateProfessionalTimeOff_Fail_EndAtIsEqualStartAt() {
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = OffsetDateTime.now();
        Long professionalId = 1L;
        Long timeOffId = 2L;
        var ex = assertThrows(DomainValidationException.class, () -> {
            new ProfessionalTimeOff(timeOffId, professionalId, "consulta", startAt, endAt);
        });
        assertThat(ex.getMessage()).isEqualTo("startAt deve ser antes de endAt");
    }
}
