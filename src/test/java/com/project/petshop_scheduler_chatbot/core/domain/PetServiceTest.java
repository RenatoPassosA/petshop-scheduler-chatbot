package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public class PetServiceTest {

    @Test
    public void CreatePetService_Success() {
        OffsetDateTime date = OffsetDateTime.now();
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, date, date);

        assertThat(petService).isNotNull();
        assertThat(petService.getId()).isNull();
        assertThat(petService.getName()).isEqualTo("tosa");
        assertThat(petService.getPrice()).isEqualByComparingTo("100.00");
        assertThat(petService.getDuration()).isEqualTo(180);
        assertThat(petService.getCreatedAt()).isEqualTo(date);
        assertThat(petService.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePetService_Success_NameTrimmed() {
        OffsetDateTime date = OffsetDateTime.now();
        PetService petService = new PetService("    tosa    ", new BigDecimal(100), 180, date, date);

        assertThat(petService).isNotNull();
        assertThat(petService.getId()).isNull();
        assertThat(petService.getName()).isEqualTo("tosa");
        assertThat(petService.getPrice()).isEqualByComparingTo("100.00");
        assertThat(petService.getDuration()).isEqualTo(180);
        assertThat(petService.getCreatedAt()).isEqualTo(date);
        assertThat(petService.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePetService_Fail_NameIsSpace() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("      ", new BigDecimal(100), 180, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Serviço é obrigatório");
    }

    @Test
    public void CreatePetService_Fail_EmptyName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("", new BigDecimal(100), 180, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Serviço é obrigatório");
    }

    @Test
    public void CreatePetService_Fail_NullName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService(null, new BigDecimal(100), 180, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do Serviço é obrigatório");
    }

    @Test
    public void CreatePetService_Fail_InvalidLowDuration() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("tosa", new BigDecimal(100), 29, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
    }

    @Test
    public void CreatePetService_Fail_InvalidHighDuration() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("tosa", new BigDecimal(100), 181, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
    }

    @Test
    public void CreatePetService_Fail_DurationNotMultiple15() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("tosa", new BigDecimal(100), 155, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Normalizar duração de serviço terminando em multiplos de 15");
    }

    @Test
    public void CreatePetService_Fail_NullPrice() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("tosa", null, 180, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Preço é obrigatório");
    }

    @Test
    public void CreatePetService_Fail_NegativePrice() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new PetService("tosa", new BigDecimal(-100), 180, date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Preço deve ser maior ou igual a 0,00");
    }

    @Test
    public void CreatePetServiceWithId_Success() {
        Long petServiceId = 10L;
        OffsetDateTime date = OffsetDateTime.now();
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, date, date);


        PetService petServiceWithId = petService.withPersistenceId(petServiceId);

        assertThat(petServiceWithId).isNotNull();
        assertThat(petServiceWithId.getId()).isEqualTo(10L);
        assertThat(petServiceWithId.getName()).isEqualTo("tosa");
        assertThat(petService.getPrice()).isEqualByComparingTo("100.00");
        assertThat(petServiceWithId.getDuration()).isEqualTo(180);
        assertThat(petServiceWithId.getCreatedAt()).isEqualTo(date);
        assertThat(petServiceWithId.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePetServiceWithId_Fail_NullId() {
        Long petServiceId = null;
        OffsetDateTime date = OffsetDateTime.now();
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            petService.withPersistenceId(petServiceId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreatePetServiceWithId_Fail_NegativeId() {
        Long petServiceId = -10L;
        OffsetDateTime date = OffsetDateTime.now();
        PetService petService = new PetService("tosa", new BigDecimal(100), 180, date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            petService.withPersistenceId(petServiceId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreatePetService_NormalizesPrice_RoundingAndScale() {
        OffsetDateTime date = OffsetDateTime.now();

        PetService serviceUp = new PetService("tosa", new BigDecimal("100.555"), 60, date, date);
        assertThat(serviceUp.getPrice())
            .isEqualByComparingTo(new BigDecimal("100.56"));

        PetService serviceDown = new PetService("tosa", new BigDecimal("100.554"), 60, date, date);
        assertThat(serviceDown.getPrice())
            .isEqualByComparingTo(new BigDecimal("100.55"));

        PetService serviceOneDecimal = new PetService("tosa", new BigDecimal("25.5"), 60, date, date);
        assertThat(serviceOneDecimal.getPrice())
            .isEqualByComparingTo(new BigDecimal("25.50"));

        PetService serviceInteger = new PetService("tosa", new BigDecimal("50"), 60, date, date);
        assertThat(serviceInteger.getPrice())
            .isEqualByComparingTo(new BigDecimal("50.00"));
    }

}
