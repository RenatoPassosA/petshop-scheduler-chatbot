package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

public class PetTest {

    @Test
    public void CreatePet_Success() {
        OffsetDateTime date = OffsetDateTime.now();
        Long tutorId = 1L;
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, "idoso", date, date);

        assertThat(pet).isNotNull();
        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isEqualTo("flor");
        assertThat(pet.getGender()).isEqualTo(Gender.F);
        assertThat(pet.getSize()).isEqualTo(PetSize.SMALL);
        assertThat(pet.getBreed()).isEqualTo("york");
        assertThat(pet.getTutorId()).isEqualTo(tutorId);
        assertThat(pet.getObservations()).isEqualTo("idoso");
        assertThat(pet.getCreatedAt()).isEqualTo(date);
        assertThat(pet.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePet_Success_NameTrimmed() {
        OffsetDateTime date = OffsetDateTime.now();
        Long tutorId = 1L;
        Pet pet = new Pet("   flor    ", Gender.F, PetSize.SMALL, "york", tutorId, "idoso", date, date);

        assertThat(pet).isNotNull();
        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isEqualTo("flor");
        assertThat(pet.getGender()).isEqualTo(Gender.F);
        assertThat(pet.getSize()).isEqualTo(PetSize.SMALL);
        assertThat(pet.getBreed()).isEqualTo("york");
        assertThat(pet.getTutorId()).isEqualTo(tutorId);
        assertThat(pet.getObservations()).isEqualTo("idoso");
        assertThat(pet.getCreatedAt()).isEqualTo(date);
        assertThat(pet.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePet_Fail_NameIsSpace() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Pet("       ", Gender.F, PetSize.SMALL, "york", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do pet é obrigatório");
    }

    @Test
    public void CreatePet_Fail_EmptyName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Pet("", Gender.F, PetSize.SMALL, "york", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do pet é obrigatório");
    }

    @Test
    public void CreatePet_Fail_NullName() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet(null, Gender.F, PetSize.SMALL, "york", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Nome do pet é obrigatório");
    }

    @Test
    public void CreatePet_Fail_NullGender() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", null, PetSize.SMALL, "york", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Sexo do pet deve ser M ou F");
    }

    @Test
    public void CreatePet_Fail_NullSize() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", Gender.F, null, "york", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Porte do pet é obrigatório");
    }

    @Test
    public void CreatePet_Fail_NullBreed() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", Gender.F, PetSize.SMALL, null, 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Raça do pet é obrigatória");
    }

    @Test
    public void CreatePet_Fail_EmptyBreed() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", Gender.F, PetSize.SMALL, "", 1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Raça do pet é obrigatória");
    }

    @Test
    public void CreatePet_Fail_NullTutorId() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", Gender.F, PetSize.SMALL, "york", null, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Inserir tutor válido");
    }

    @Test
    public void CreatePet_Fail_NegativeTutorId() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
             new Pet("flor", Gender.F, PetSize.SMALL, "york", -1L, "idoso", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Inserir tutor válido");
    }

    @Test
    public void CreatePetWithId_Success() {
        Long petId = 10L;
        Long tutorId = 1L;
        OffsetDateTime date = OffsetDateTime.now();
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, "idoso", date, date);


        Pet petWithId = pet.withPersistenceId(petId);

        assertThat(petWithId).isNotNull();
        assertThat(petWithId.getId()).isEqualTo(10L);
        assertThat(petWithId.getName()).isEqualTo("flor");
        assertThat(petWithId.getGender()).isEqualTo(Gender.F);
        assertThat(petWithId.getSize()).isEqualTo(PetSize.SMALL);
        assertThat(petWithId.getBreed()).isEqualTo("york");
        assertThat(petWithId.getTutorId()).isEqualTo(tutorId);
        assertThat(petWithId.getObservations()).isEqualTo("idoso");
        assertThat(petWithId.getCreatedAt()).isEqualTo(date);
        assertThat(petWithId.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreatePetWithId_Fail_NullId() {
        Long tutorId = 1L;
        Long petId = null;
        OffsetDateTime date = OffsetDateTime.now();
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, "idoso", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            pet.withPersistenceId(petId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreatePetWithId_Fail_NegativeId() {
        Long tutorId = 1L;
        Long petId = -10L;
        OffsetDateTime date = OffsetDateTime.now();
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, "idoso", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            pet.withPersistenceId(petId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }
}
