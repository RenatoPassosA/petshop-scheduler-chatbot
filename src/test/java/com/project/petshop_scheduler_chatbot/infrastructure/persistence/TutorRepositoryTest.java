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

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.TutorMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.TutorEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.TutorRepositoryJpa;

@Testcontainers
@DataJpaTest
@Import({TutorRepositoryJpa.class, TutorMapper.class})
public class TutorRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TutorRepositoryJpa tutorRepositoryJpa;

    @BeforeEach
    void clearDB(@Autowired TutorEntityRepository tutorEntityRepository) {
        tutorEntityRepository.deleteAll();
    }

    private Tutor GenerateTutor(String name, String phone, String address) {
        return new Tutor(name, new PhoneNumber(phone), address, OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        Tutor tutor = GenerateTutor("renato", "21988398302", "rua 1");

        Tutor persistedTutor = tutorRepositoryJpa.save(tutor);

        assertThat(persistedTutor).isNotNull();
        assertThat(persistedTutor.getId()).isNotNull();

        Optional<Tutor> check = tutorRepositoryJpa.findById(persistedTutor.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getName()).isEqualTo("renato");
        assertThat(check.get().getPhoneNumber().value()).isEqualTo("+5521988398302");
        assertThat(check.get().getAddress()).isEqualTo("rua 1");
    }

    @Test
    void findByPhone_Sucess_ShouldReturnTutor() {
        Tutor tutor = GenerateTutor("renato", "21988398302", "rua 1");
        Tutor persistedTutor = tutorRepositoryJpa.save(tutor);

        Optional<Tutor> check =
                tutorRepositoryJpa.findByPhone(new PhoneNumber("+5521988398302"));

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedTutor.getId());
    }

    @Test
    void findByPhone_ErrorWhenPhoneNotExists_ShouldReturnEmpty() {
        Tutor tutor = GenerateTutor("renato", "21988398302", "rua 1");
        tutorRepositoryJpa.save(tutor);

        Optional<Tutor> check =
                tutorRepositoryJpa.findByPhone(new PhoneNumber("+55111111111"));

        assertThat(check).isNotPresent();
        assertThat(check).isEqualTo(Optional.empty());
    }

    @Test
    void existsByPhone_Sucess_ShouldReturnTrue() {
        PhoneNumber number = new PhoneNumber("21988398302");


        tutorRepositoryJpa.save(GenerateTutor("renato", "21988398302", "rua 1"));

        boolean exists = tutorRepositoryJpa.existsByPhone(number);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByPhone_Error_ShouldReturnFalse() {
        tutorRepositoryJpa.save(GenerateTutor("renato", "21988398302", "rua 1"));

        boolean exists = tutorRepositoryJpa.existsByPhone(new PhoneNumber("1111111"));

        assertThat(exists).isFalse();
    }

    @Test
    void deleteById_Sucess_ShouldDeleteTutor() {
        Tutor persistedTutor = tutorRepositoryJpa.save(GenerateTutor("renato", "21988398302", "rua 1"));
        Long tutorId = persistedTutor.getId();
        assertThat(tutorRepositoryJpa.findById(tutorId)).isPresent();

        tutorRepositoryJpa.deleteById(tutorId);

        assertThat(tutorRepositoryJpa.findById(tutorId)).isNotPresent();
    }

    @Test
    void getAll_Sucess_ShouldReturnAllPersistedTutors() {
        tutorRepositoryJpa.save(GenerateTutor("Tutor 1", "21911111111", "rua 1"));
        tutorRepositoryJpa.save(GenerateTutor("Tutor 2", "21922222222", "rua 2"));

        List<Tutor> tutores = tutorRepositoryJpa.getAll();

        assertThat(tutores).hasSize(2);
        assertThat(tutores)
                .extracting(Tutor::getName)
                .containsExactlyInAnyOrder("Tutor 1", "Tutor 2");
    }

    @Test
    void getAll_EmptyDB_ShouldReturnEmptyList() {
        List<Tutor> tutores = tutorRepositoryJpa.getAll();

        assertThat(tutores).hasSize(0);
        assertThat(tutores).isEmpty();
    }
}
