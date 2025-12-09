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

import com.project.petshop_scheduler_chatbot.core.domain.Appointment;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.AppointmentMapper;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.AppointmentEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.AppointmentRepositoryJpa;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.PetEntityRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository.ProfessionalEntityRepository;

@Testcontainers
@DataJpaTest
@Import({AppointmentRepositoryJpa.class, AppointmentMapper.class})
public class AppointmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private AppointmentRepositoryJpa appointmentRepositoryJpa;

    @Autowired
    private ProfessionalEntityRepository professionalEntityRepository;

    @Autowired
    private PetEntityRepository petEntityRepository;

    @BeforeEach
    void clearDB(@Autowired AppointmentEntityRepository appointmentEntityRepository) {
        appointmentEntityRepository.deleteAll();
    }

    private Appointment GenerateAppointment(Long petId, Long professionalId, OffsetDateTime startAt) {
        OffsetDateTime provided = OffsetDateTime.now();
        if (startAt == null)
            startAt = OffsetDateTime.parse("2025-12-11T12:30:00Z");
        if (petId == null)
            petId = 2L;
        Long tutorId = 3L;
        if (professionalId == null)
            professionalId = 4L;
        Long serviceId = 5L;
        return new Appointment(petId, tutorId, professionalId, serviceId, startAt, 120, AppointmentStatus.SCHEDULED, "nenhuma", provided, provided);
    }

    private Long PersistProfessionalAndReturnId() {
        OffsetDateTime date = OffsetDateTime.now();
        ProfessionalEntity professionalEntity = professionalEntityRepository.save(new ProfessionalEntity("renato", Office.VET, date, date));
        return professionalEntity.getId();
    }

    private Long PersistPetAndReturnId() {
        OffsetDateTime date = OffsetDateTime.now();
        PetEntity petEntity = petEntityRepository.save(new PetEntity("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok", date, date));

        return petEntity.getId();
    }

    @Test
    void save_SucessShouldPersistAndGenereteID() {
        Appointment appointment = GenerateAppointment(null, null, null);
        Appointment persistedAppointment = appointmentRepositoryJpa.save(appointment);

        assertThat(persistedAppointment).isNotNull();
        assertThat(persistedAppointment.getId()).isNotNull();

        Optional<Appointment> check = appointmentRepositoryJpa.findById(persistedAppointment.getId());
        assertThat(check).isPresent();
        assertThat(check.get().getPetId()).isEqualTo(2L);
        assertThat(check.get().getTutorId()).isEqualTo(3L);
        assertThat(check.get().getProfessionalId()).isEqualTo(4L);
        assertThat(check.get().getServiceId()).isEqualTo(5L);
        assertThat(check.get().getStartAt()).isEqualTo(OffsetDateTime.parse("2025-12-11T12:30:00Z"));
        assertThat(check.get().getServiceDuration()).isEqualTo(120);
        assertThat(check.get().getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(check.get().getObservations()).isEqualTo("nenhuma");
    }

    @Test
    void findById_Sucess_ShouldReturnAppointment() {
        Appointment appointment = GenerateAppointment(null, null, null);
        Appointment persistedAppointment = appointmentRepositoryJpa.save(appointment);

        Optional<Appointment> check = appointmentRepositoryJpa.findById(persistedAppointment.getId());

        assertThat(check).isPresent();
        assertThat(check.get().getId()).isEqualTo(persistedAppointment.getId());
    }

    @Test
    void findByTutorId_Sucess_ShouldReturnAppointment() {
        Appointment persistedAppointment = appointmentRepositoryJpa.save(GenerateAppointment(null, null, null));

        List<Appointment> check = appointmentRepositoryJpa.findByTutorId(3L);

        
        assertThat(check)
        .hasSize(1)
        .first()
        .extracting(Appointment::getId)
        .isEqualTo(persistedAppointment.getId());
    }

    @Test
    void existsOverlapForProfessional_ShouldReturnTrue_WhenIntervalsOverlap() {
        Long professionalId = PersistProfessionalAndReturnId();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");

        appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startAt));
       
        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T13:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T15:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessional(professionalId, checkStart, checkEnd);

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlapForProfessional_ShouldReturnFalse_WhenIntervalsDoNotOverlap() {
        Long professionalId = PersistProfessionalAndReturnId();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        
        appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T10:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T11:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessional(professionalId, checkStart, checkEnd);

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForProfessional_ShouldReturnFalse_WhenEndIsTheSameAsTheAppoointmentStart() {
        Long professionalId = PersistProfessionalAndReturnId();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        
        appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T11:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T12:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessional(professionalId, checkStart, checkEnd);

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForPet_ShouldReturnTrue_WhenIntervalsOverlap() {
        Long professionalId = PersistProfessionalAndReturnId();
        Long petId = PersistPetAndReturnId();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        
        appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T14:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForPet(petId, checkStart, checkEnd);

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlapForPet_ShouldReturnFalse_WhenIntervalsDoNotOverlap() {
        Long professionalId = PersistProfessionalAndReturnId();
        Long petId = PersistPetAndReturnId();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        
        appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T08:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T10:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForPet(petId, checkStart, checkEnd);

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForProfessionalExcluding_ShouldReturnFalse_WhenOnlyOverlapIsExcludedAppointment() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointmentWithId = appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T13:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T15:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessionalExcluding(professionalId, checkStart, checkEnd, appointmentWithId.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForProfessionalExcluding_ShouldReturnTrue_WhenAnotherAppointmentOverlaps() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startA = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointment1 = appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startA)); //appointment1 nao conflitante

        OffsetDateTime startB = OffsetDateTime.parse("2025-01-10T13:00:00Z");
        appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startB)); //appointment2 conflitante

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T13:30:00Z"); //horarios conflitantes com 2, mesmo exluindo o 1
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T14:30:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessionalExcluding(professionalId, checkStart, checkEnd, appointment1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlapForProfessionalExcluding_ShouldReturnFalse_WhenNoOtherAppointmentsOverlap() {
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, startAt));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T08:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T10:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForProfessionalExcluding(professionalId, checkStart, checkEnd, appointment.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForPetExcluding_ShouldReturnFalse_WhenOnlyOverlapIsExcludedAppointment() {
        Long petId = 10L;
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startAt));

        // Janela que conflitaria com A
        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T13:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T15:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForPetExcluding(petId, checkStart, checkEnd, appointment.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlapForPetExcluding_ShouldReturnTrue_WhenAnotherAppointmentOverlaps() {
        Long petId = 10L;
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startA = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startA));

        OffsetDateTime startB = OffsetDateTime.parse("2025-01-10T13:00:00Z");
        appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startB));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T13:30:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T14:30:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForPetExcluding(petId, checkStart, checkEnd, appointment.getId() );

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlapForPetExcluding_ShouldReturnFalse_WhenNoOverlap() {
        Long petId = 10L;
        Long professionalId = PersistProfessionalAndReturnId();

        OffsetDateTime startA = OffsetDateTime.parse("2025-01-10T12:00:00Z");
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(petId, professionalId, startA));

        OffsetDateTime checkStart = OffsetDateTime.parse("2025-01-10T08:00:00Z");
        OffsetDateTime checkEnd   = OffsetDateTime.parse("2025-01-10T10:00:00Z");

        boolean exists = appointmentRepositoryJpa.existsOverlapForPetExcluding(petId, checkStart, checkEnd, appointment.getId()
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOwnership_ShouldReturnTrue() {
        Long tutorId = 3L;
        Long professionalId = PersistProfessionalAndReturnId();
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, null));

        boolean exists = appointmentRepositoryJpa.existsOwnership(tutorId, appointment.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsOwnership_ShouldReturnFalse() {
        Long tutorId = 999L;
        Long professionalId = PersistProfessionalAndReturnId();
        Appointment appointment = appointmentRepositoryJpa.save(GenerateAppointment(null, professionalId, null));

        boolean exists = appointmentRepositoryJpa.existsOwnership(tutorId, appointment.getId());

        assertThat(exists).isFalse();
    }
}
