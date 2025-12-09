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

import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.ProfessionalTimeOffMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalTimeOffEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalTimeOffRepositoryJpa;

@Testcontainers
@DataJpaTest
@Import({ProfessionalTimeOffRepositoryJpa.class, ProfessionalTimeOffMapper.class})
public class ProfessionalTimeOffRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ProfessionalTimeOffRepositoryJpa professionalTimeOffRepositoryJpa;

    @Autowired
    private ProfessionalEntityRepository professionalEntityRepository;

    @BeforeEach
    void clearDB(@Autowired ProfessionalTimeOffEntityRepository professionalTimeOffEntityRepository) {
        professionalTimeOffEntityRepository.deleteAll();
    }

    private ProfessionalTimeOff GenerateTimeOff(Long professionalId, String reason) {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-20T10:00:00Z");
        OffsetDateTime endAt   = OffsetDateTime.parse("2025-01-23T12:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        return new ProfessionalTimeOff(professionalId, reason, startAt, endAt, date, date);
    }

    private Long PersistProfessionalAndReturnId() {
        OffsetDateTime date = OffsetDateTime.now();
        ProfessionalEntity professionalEntity = professionalEntityRepository.save(new ProfessionalEntity("renato", Office.VET, date, date));
        return professionalEntity.getId();
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        Long professionalId = PersistProfessionalAndReturnId();
        ProfessionalTimeOff timeOff = GenerateTimeOff(professionalId, "consulta medica");
        ProfessionalTimeOff persistedTimeOff = professionalTimeOffRepositoryJpa.save(timeOff);

        assertThat(persistedTimeOff).isNotNull();
        assertThat(persistedTimeOff.getId()).isNotNull();

        Optional<ProfessionalTimeOff> check = professionalTimeOffRepositoryJpa.findById(persistedTimeOff.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getProfessionalId()).isEqualTo(1L);
        assertThat(check.get().getReason()).isEqualTo("consulta medica");
    }

    @Test
    void findById_Sucess_ShouldReturnTimeOff() {
        Long professionalId = PersistProfessionalAndReturnId();
        ProfessionalTimeOff timeOff = GenerateTimeOff(professionalId, "consulta medica");
        ProfessionalTimeOff persistedTimeOff = professionalTimeOffRepositoryJpa.save(timeOff);

        assertThat(persistedTimeOff).isNotNull();
        assertThat(persistedTimeOff.getId()).isNotNull();

        Optional<ProfessionalTimeOff> check = professionalTimeOffRepositoryJpa.findById(persistedTimeOff.getId());

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedTimeOff.getId());
        assertThat(check.get().getReason()).isEqualTo("consulta medica");
    }

    @Test
    void existsOverlap_Success_WhenIntervalsOverlap_ShouldReturnTrue() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime existingTimeOffStart = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime existingTimeOffEnd = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        ProfessionalTimeOff timeOff = new ProfessionalTimeOff(professionalId, "consulta médica", existingTimeOffStart, existingTimeOffEnd, date, date);
        professionalTimeOffRepositoryJpa.save(timeOff);

        OffsetDateTime newStart = OffsetDateTime.parse("2025-01-01T11:00:00Z");
        OffsetDateTime newEnd   = OffsetDateTime.parse("2025-01-01T13:00:00Z");

        boolean exists = professionalTimeOffRepositoryJpa.existsOverlap(professionalId, newStart, newEnd);

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlap_WhenIntervalsDontOverlap_ShouldReturnFalse() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime existingTimeOffStart = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime existingTimeOffEnd = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        ProfessionalTimeOff timeOff = new ProfessionalTimeOff(professionalId, "consulta médica", existingTimeOffStart, existingTimeOffEnd, date, date);
        professionalTimeOffRepositoryJpa.save(timeOff);

        OffsetDateTime newStart = OffsetDateTime.parse("2025-01-01T13:00:00Z");
        OffsetDateTime newEnd   = OffsetDateTime.parse("2025-01-01T17:00:00Z");

        boolean exists = professionalTimeOffRepositoryJpa.existsOverlap(professionalId, newStart, newEnd);

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlap_WhenNewStartsExactlyAtExistingEnd_ShouldReturnFalse() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime existingTimeOffStart = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime existingTimeOffEnd = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        ProfessionalTimeOff timeOff = new ProfessionalTimeOff(professionalId, "consulta médica", existingTimeOffStart, existingTimeOffEnd, date, date);
        professionalTimeOffRepositoryJpa.save(timeOff);

        OffsetDateTime newStart = OffsetDateTime.parse("2025-01-01T12:00:00Z");
        OffsetDateTime newEnd   = OffsetDateTime.parse("2025-01-01T13:00:00Z");

        boolean exists = professionalTimeOffRepositoryJpa.existsOverlap(professionalId, newStart, newEnd);

        assertThat(exists).isFalse();
    }

    @Test
    void getAllByProfessionalId_Sucess_ShouldReturnAllTimeOffs() {
        Long professionalId = PersistProfessionalAndReturnId();
        professionalTimeOffRepositoryJpa.save(GenerateTimeOff(professionalId, "consulta medica 1"));
        professionalTimeOffRepositoryJpa.save(GenerateTimeOff(professionalId, "consulta medica 2"));
        professionalTimeOffRepositoryJpa.save(GenerateTimeOff(professionalId, "consulta medica 3"));
        

        List<ProfessionalTimeOff> timeOffs = professionalTimeOffRepositoryJpa.findAllByProfessionalId(professionalId);

        assertThat(timeOffs).hasSize(3);
        assertThat(timeOffs)
                .extracting(ProfessionalTimeOff::getReason)
                .containsExactlyInAnyOrder("consulta medica 1", "consulta medica 2", "consulta medica 3");
    }

    @Test
    void getAll_EmptyDB_ShouldReturnEmptyList() {
        Long professionalId = PersistProfessionalAndReturnId();;
        List<ProfessionalTimeOff> timeOffs = professionalTimeOffRepositoryJpa.findAllByProfessionalId(professionalId);

        assertThat(timeOffs).hasSize(0);
        assertThat(timeOffs).isEmpty();
    }

    @Test
    void deleteById_Sucess_ShouldDeleteProfessionalTimeOff() {
        Long professionalId = PersistProfessionalAndReturnId();
        ProfessionalTimeOff persistedProfessionalTimeOff = professionalTimeOffRepositoryJpa.save(GenerateTimeOff(professionalId, "consulta medica 1"));
        Long professionalTimeOffId = persistedProfessionalTimeOff.getId();
        assertThat(professionalTimeOffRepositoryJpa.findById(professionalTimeOffId)).isPresent();

        professionalTimeOffRepositoryJpa.deleteById(professionalTimeOffId);

        assertThat(professionalTimeOffRepositoryJpa.findById(professionalTimeOffId)).isNotPresent();
    }
}