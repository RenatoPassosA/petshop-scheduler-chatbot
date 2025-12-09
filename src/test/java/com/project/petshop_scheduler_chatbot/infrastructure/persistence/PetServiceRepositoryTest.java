package com.project.petshop_scheduler_chatbot.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.PetServiceMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.PetServiceEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.PetServiceRepositoryJpa;

@Testcontainers
@DataJpaTest
@Import({PetServiceRepositoryJpa.class, PetServiceMapper.class})
public class PetServiceRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private PetServiceRepositoryJpa petServiceRepositoryJpa;

    @BeforeEach
    void clearDB(@Autowired PetServiceEntityRepository petServiceEntityRepository) {
        petServiceEntityRepository.deleteAll();
    }

    private PetService GeneratePetService(String name, Long price, int duration) {
        OffsetDateTime provided = OffsetDateTime.now();
        return new PetService(name, new BigDecimal(price), duration, provided, provided);
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        PetService petService = GeneratePetService("tosa", 100L, 180);
        PetService persistedPetService = petServiceRepositoryJpa.save(petService);

        assertThat(persistedPetService).isNotNull();
        assertThat(persistedPetService.getId()).isNotNull();

        Optional<PetService> check = petServiceRepositoryJpa.findById(persistedPetService.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getName()).isEqualTo("tosa");
        assertThat(check.get().getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(check.get().getDuration()).isEqualTo(180);
    }

    @Test
    void findById_Sucess_ShouldReturnPetService() {
        PetService petService = GeneratePetService("tosa", 100L, 180);
        PetService persistedPetService = petServiceRepositoryJpa.save(petService);

        Optional<PetService> check = petServiceRepositoryJpa.findById(persistedPetService.getId());

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedPetService.getId());
    }

    @Test
    void findByName_Sucess_ShouldReturnPetService() {
        petServiceRepositoryJpa.save(GeneratePetService("tosa", 100L, 180));

        List<PetService> check = petServiceRepositoryJpa.findByName("tosa");

        assertThat(check).hasSize(1);
        assertThat(check)
                .extracting(PetService::getName)
                .containsExactlyInAnyOrder("tosa");
    }

    @Test
    void findByName_NotFoundWhenServiceNotExists_ShouldReturnEmpty() {
        List<PetService> check = petServiceRepositoryJpa.findByName("tosa");

        assertThat(check).hasSize(0);
        assertThat(check).isEmpty();
    }

    @Test
    void searchAllCommonName_Sucess_ShouldReturnPetServices() {
        petServiceRepositoryJpa.save(GeneratePetService("tosa", 100L, 180));
        petServiceRepositoryJpa.save(GeneratePetService("tosa premium", 100L, 180));
        petServiceRepositoryJpa.save(GeneratePetService("tosa higienica", 100L, 180));

        List<PetService> check = petServiceRepositoryJpa.searchByName("tosa");

        assertThat(check).hasSize(3);
        assertThat(check)
                .extracting(PetService::getName)
                .containsExactlyInAnyOrder("tosa", "tosa premium", "tosa higienica");
    }

    @Test
    void deleteById_Sucess_ShouldDeletePetService() {
        PetService persistedPetService = petServiceRepositoryJpa.save(GeneratePetService("tosa", 100L, 180));
        Long petServiceId = persistedPetService.getId();
        assertThat(petServiceRepositoryJpa.findById(petServiceId)).isPresent();

        petServiceRepositoryJpa.deleteById(petServiceId);

        assertThat(petServiceRepositoryJpa.findById(petServiceId)).isNotPresent();
    }

    @Test
    void getAll_Sucess_ShouldReturnAllPersistedPetServices() {
        petServiceRepositoryJpa.save(GeneratePetService("service 1", 100L, 180));
        petServiceRepositoryJpa.save(GeneratePetService("service 2", 100L, 180));

        List<PetService> petServices = petServiceRepositoryJpa.getAll();

        assertThat(petServices).hasSize(2);
        assertThat(petServices)
                .extracting(PetService::getName)
                .containsExactlyInAnyOrder("service 1", "service 2");
    }

    @Test
    void getAll_EmptyDB_ShouldReturnEmptyList() {
        List<PetService> petServices = petServiceRepositoryJpa.getAll();

        assertThat(petServices).hasSize(0);
        assertThat(petServices).isEmpty();
    }
}
