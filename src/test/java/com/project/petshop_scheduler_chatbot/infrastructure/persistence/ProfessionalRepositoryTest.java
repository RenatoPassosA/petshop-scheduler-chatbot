package com.project.petshop_scheduler_chatbot.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.ProfessionalMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalRepositoryJpa;

@Testcontainers
@DataJpaTest
@Import({ProfessionalRepositoryJpa.class, ProfessionalMapper.class})
public class ProfessionalRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ProfessionalRepositoryJpa professionalRepositoryJpa;

    @BeforeEach
    void clearDB(@Autowired ProfessionalEntityRepository professionalEntityRepository) {
        professionalEntityRepository.deleteAll();
    }

    private Professional GenerateProfessional(String name, Office function) {
        OffsetDateTime provided = OffsetDateTime.now();
        return new Professional(name, function, provided, provided);
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        Professional professional = GenerateProfessional("renato", Office.VET);
        Professional persistedProfessional = professionalRepositoryJpa.save(professional);

        assertThat(persistedProfessional).isNotNull();
        assertThat(persistedProfessional.getId()).isNotNull();

        Optional<Professional> check = professionalRepositoryJpa.findById(persistedProfessional.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getName()).isEqualTo("renato");
        assertThat(check.get().getFunction()).isEqualTo(Office.VET);
    }

    @Test
    void findById_Sucess_ShouldReturnProfessional() {
        Professional professional = GenerateProfessional("renato", Office.VET);
        Professional persistedProfessional = professionalRepositoryJpa.save(professional);

        Optional<Professional> check = professionalRepositoryJpa.findById(persistedProfessional.getId());

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedProfessional.getId());
    }

    @Test
    void findByName_Sucess_ShouldReturnProfessional() {
        professionalRepositoryJpa.save(GenerateProfessional("renato", Office.VET));

        List<Professional> check = professionalRepositoryJpa.findByName("renato");

        assertThat(check).hasSize(1);
        assertThat(check)
                .extracting(Professional::getName)
                .containsExactlyInAnyOrder("renato");
    }

    @Test
    void findByName_NotFoundWhenProfessionalNotExists_ShouldReturnEmpty() {
        List<Professional> check = professionalRepositoryJpa.findByName("renato");

        assertThat(check).hasSize(0);
        assertThat(check).isEmpty();
    }

    @Test
    void deleteById_Sucess_ShouldDeleteProfessional() {
        Professional persistedProfessional = professionalRepositoryJpa.save(GenerateProfessional("renato", Office.VET));
        Long professionalId = persistedProfessional.getId();
        assertThat(professionalRepositoryJpa.findById(professionalId)).isPresent();

        professionalRepositoryJpa.deleteById(professionalId);

        assertThat(professionalRepositoryJpa.findById(professionalId)).isNotPresent();
    }

    @Test
    void getAll_Sucess_ShouldReturnAllPersistedProfessionals() {
        professionalRepositoryJpa.save(GenerateProfessional("professional 1", Office.VET));
        professionalRepositoryJpa.save(GenerateProfessional("professional 2", Office.VET));

        List<Professional> professionals = professionalRepositoryJpa.getAll();

        assertThat(professionals).hasSize(2);
        assertThat(professionals)
                .extracting(Professional::getName)
                .containsExactlyInAnyOrder("professional 1", "professional 2");
    }

    @Test
    void getAll_EmptyDB_ShouldReturnEmptyList() {
        List<Professional> professionals = professionalRepositoryJpa.getAll();

        assertThat(professionals).hasSize(0);
        assertThat(professionals).isEmpty();
    }
}