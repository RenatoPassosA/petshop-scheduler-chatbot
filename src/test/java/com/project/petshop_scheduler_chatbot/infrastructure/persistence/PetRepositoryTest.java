package com.project.petshop_scheduler_chatbot.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.PetMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.PetEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.PetRepositoryJpa;

@Testcontainers
@DataJpaTest
@Import({PetRepositoryJpa.class, PetMapper.class})
public class PetRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private PetRepositoryJpa petRepositoryJpa;

    @BeforeEach
    void clearDB(@Autowired PetEntityRepository petEntityRepository) {
        petEntityRepository.deleteAll();
    }

    private Pet GeneratePet(String name, Long tutorId) {
        OffsetDateTime provided = OffsetDateTime.now();
        return new Pet(name, Gender.F, PetSize.SMALL, "york", tutorId, null, provided, provided);
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        Long tutorId = 1L;
        Pet pet = GeneratePet("flor", tutorId);

        Pet persistedPet = petRepositoryJpa.save(pet);

        assertThat(persistedPet).isNotNull();
        assertThat(persistedPet.getId()).isNotNull();

        Optional<Pet> check = petRepositoryJpa.findById(persistedPet.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getName()).isEqualTo("flor");
        assertThat(check.get().getGender()).isEqualTo(Gender.F);
        assertThat(check.get().getSize()).isEqualTo(PetSize.SMALL);
        assertThat(check.get().getBreed()).isEqualTo("york");
        assertThat(check.get().getTutorId()).isEqualTo(1L);
    }

    @Test
    void findById_Sucess_ShouldReturnPet() {
        Long tutorId = 1L;
        Pet persistedPet = petRepositoryJpa.save(GeneratePet("flor", tutorId));

        Optional<Pet> check = petRepositoryJpa.findById(persistedPet.getId());

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedPet.getId());
    }

    @Test
    void listByTutor_Sucess_ShouldReturnPopulatedList() {
        Long tutorId = 1L;
        petRepositoryJpa.save(GeneratePet("pet 1", tutorId));
        petRepositoryJpa.save(GeneratePet("pet 2", tutorId));

        List<Pet> samePetsTutor = petRepositoryJpa.listByTutor(tutorId);

        assertThat(samePetsTutor).hasSize(2);
        assertThat(samePetsTutor)
                .extracting(Pet::getName)
                .containsExactlyInAnyOrder("pet 1", "pet 2");
    }

    @Test
    void listByTutor_Error_ShouldReturnEmptyList() {
        Long tutorId = 1L;

        List<Pet> samePetsTutor = petRepositoryJpa.listByTutor(tutorId);

        assertThat(samePetsTutor).hasSize(0);
        assertThat(samePetsTutor).isEmpty();
                
    }

    @Test
    void existsByIdAndTutorId_Sucess_ShouldReturnTrue() {
        Long tutorId = 1L;
        Pet persistedPet = petRepositoryJpa.save(GeneratePet("flor", tutorId));
        Long petId = persistedPet.getId();

        boolean exists = petRepositoryJpa.existsByIdAndTutorId(petId, tutorId);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndTutorId_Error_ShouldReturnFaLse() {
        Long tutorId = 1L;
        Long petId = 2L;

        boolean exists = petRepositoryJpa.existsByIdAndTutorId(petId, tutorId);

        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_Sucess_ShouldDeletePet() {
        Long tutorId = 1L;
        Pet persistedPet = petRepositoryJpa.save(GeneratePet("flor", tutorId));
        Long petId = persistedPet.getId();
        assertThat(petRepositoryJpa.findById(petId)).isPresent();

        petRepositoryJpa.deleteById(petId);

        assertThat(petRepositoryJpa.findById(petId)).isNotPresent();
    }

    @Test
    void getAll_Sucess_ShouldReturnAllPersistedPets() {
        Long tutorId = 1L;
        petRepositoryJpa.save(GeneratePet("pet 1", tutorId));
        petRepositoryJpa.save(GeneratePet("pet 2", tutorId));

        List<Pet> pets = petRepositoryJpa.getAll();

        assertThat(pets).hasSize(2);
        assertThat(pets)
                .extracting(Pet::getName)
                .containsExactlyInAnyOrder("pet 1", "pet 2");
    }

    @Test
    void getAll_EmptyDB_ShouldReturnEmptyList() {
        List<Pet> pets = petRepositoryJpa.getAll();

        assertThat(pets).hasSize(0);
        assertThat(pets).isEmpty();
    }
}