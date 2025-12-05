package com.project.petshop_scheduler_chatbot.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

public class AppointmentTest {

    @Test
    public void CreateAppointment_Success() {
        OffsetDateTime startAt = OffsetDateTime.now().withMinute(15).withSecond(0).withNano(0);
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        int serviceDuration = 180;
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, startAt, serviceDuration, AppointmentStatus.SCHEDULED, "nenhuma", date, date);

        assertThat(appointment).isNotNull();
        assertThat(appointment.getId()).isNull();
        assertThat(appointment.getPetId()).isEqualTo(2L);
        assertThat(appointment.getTutorId()).isEqualTo(3L);
        assertThat(appointment.getProfessionalId()).isEqualTo(4L);
        assertThat(appointment.getServiceId()).isEqualTo(5L);
        assertThat(appointment.getStartAt()).isEqualTo(startAt);
        assertThat(appointment.getServiceDuration()).isEqualTo(180);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(appointment.getObservations()).isEqualTo("nenhuma");
        assertThat(appointment.getCreatedAt()).isEqualTo(date);
        assertThat(appointment.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateAppointment_Fail_PetIdNull() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(null, 3L, 4l, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um pet");
    }

    @Test
    public void CreateAppointment_Fail_PetIdNegative() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(-1L, 3L, 4l, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um pet");
    }

    @Test
    public void CreateAppointment_Fail_TutorIdNull() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, null, 4l, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um tutor");
    }

    @Test
    public void CreateAppointment_Fail_TutorIdNegative() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, -3L, 4l, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um tutor");
    }

    @Test
    public void CreateAppointment_Fail_ProfessionalIdNull() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 2L, null, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um profissional");
    }

    @Test
    public void CreateAppointment_Fail_ProfessionalIdNegative() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 2L, -4L, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um profissional");
    }

    @Test
    public void CreateAppointment_Fail_ServiceIdNull() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, null, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um serviço");
    }

    @Test
    public void CreateAppointment_Fail_ServiceIdNegative() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, -5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário vincular um serviço");
    }

    @Test
    public void CreateAppointment_Fail_StartAtNull() {
        OffsetDateTime date = OffsetDateTime.now().withMinute(15).withSecond(0).withNano(0);
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, 5L, null, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário horario do agendamento");
    }

    @Test
    public void CreateAppointment_Fail_ServiceDurationBellow30() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, 5L, date, 29, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário tempo de duração correto do serviço");
    }

    @Test
    public void CreateAppointment_Fail_ServiceDurationDoesntMultiple15() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, 5L, date, 155, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário tempo de duração correto do serviço");
    }

    @Test
    public void CreateAppointment_Fail_StatusNull() {
        OffsetDateTime date = OffsetDateTime.now();
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, 5L, date, 150, null, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Necessário status do agendamento");
    }

    @Test
    public void CreateAppointment_Fail_StartAtNotModule15() {
        OffsetDateTime date = OffsetDateTime.now().withMinute(7).withSecond(0).withNano(0);
        var ex = assertThrows(DomainValidationException.class, () -> {
            new Appointment(1L, 3L, 4L, 5L, date, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Horário de marcação deve ter minutos 00, 15, 30 ou 45");
    }

    @Test
    public void rescheduleAppointment_Success() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        int serviceDuration = 180;
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, startAt, serviceDuration, AppointmentStatus.SCHEDULED, "nenhuma", date, date);

        OffsetDateTime newStartAt = date.plusMinutes(60);

        appointment.rescheduleTo(newStartAt, date);

        assertThat(appointment).isNotNull();
        assertThat(appointment.getId()).isNull();
        assertThat(appointment.getPetId()).isEqualTo(2L);
        assertThat(appointment.getTutorId()).isEqualTo(3L);
        assertThat(appointment.getProfessionalId()).isEqualTo(4L);
        assertThat(appointment.getServiceId()).isEqualTo(5L);
        assertThat(appointment.getStartAt()).isEqualTo(newStartAt);
        assertThat(appointment.getServiceDuration()).isEqualTo(180);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(appointment.getObservations()).isEqualTo("nenhuma");
        assertThat(appointment.getCreatedAt()).isEqualTo(date);
        assertThat(appointment.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void rescheduleAppointment_Fail_NewStartAtNull() {
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime newStartAt = null;
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.rescheduleTo(newStartAt, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Novo horário inválido");
    }

    @Test
    public void rescheduleAppointment_Fail_NewStartAtIsEqual() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime newStartAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.rescheduleTo(newStartAt, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Novo horário inválido");
    }

    @Test
    public void rescheduleAppointment_Fail_NewStartAtIsBefore() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime newStartAt = date.minusMinutes(60);
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.rescheduleTo(newStartAt, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Novo horário inválido");
    }

    @Test
    public void rescheduleAppointment_Fail_AppointmentCancelled() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime newStartAt = date.plusMinutes(60);
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.CANCELLED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.rescheduleTo(newStartAt, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Consulta inválida, favor agendar outra");
    }

    @Test
    public void rescheduleAppointment_Fail_AppointmentCompleted() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime newStartAt = date.plusMinutes(60);
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.COMPLETED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.rescheduleTo(newStartAt, date);
        });
        assertThat(ex.getMessage()).isEqualTo("Consulta inválida, favor agendar outra");
    }

    @Test
    public void cancelAppointment_Fail_AppointmentCanceled() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.CANCELLED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.cancelSchedule(date);
        });
        assertThat(ex.getMessage()).isEqualTo("Consulta já encerrada");
    }

    @Test
    public void cancelAppointment_Fail_AppointmentCompleted() {
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.COMPLETED, "nenhuma", date, date);
        var ex = assertThrows(DomainValidationException.class, () -> {
           appointment.cancelSchedule(date);
        });
        assertThat(ex.getMessage()).isEqualTo("Consulta já encerrada");
    }

    @Test
    public void CreateAppointmentWithId_Success() {
        Long appointmentId = 10L;
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        Long petId = 2L;
        Long tutorId = 3L;
        Long professionalId = 4L;
        Long serviceId = 5L;
        int serviceDuration = 180;
        OffsetDateTime date = OffsetDateTime.now();
        Appointment appointment = new Appointment(petId, tutorId, professionalId, serviceId, startAt, serviceDuration, AppointmentStatus.SCHEDULED, "nenhuma", date, date);

        Appointment appointmentWithId = appointment.withPersistenceId(appointmentId);      

        assertThat(appointmentWithId).isNotNull();
        assertThat(appointmentWithId.getId()).isEqualTo(10L);
        assertThat(appointmentWithId.getPetId()).isEqualTo(2L);
        assertThat(appointmentWithId.getTutorId()).isEqualTo(3L);
        assertThat(appointmentWithId.getProfessionalId()).isEqualTo(4L);
        assertThat(appointmentWithId.getServiceId()).isEqualTo(5L);
        assertThat(appointmentWithId.getStartAt()).isEqualTo(startAt);
        assertThat(appointmentWithId.getServiceDuration()).isEqualTo(180);
        assertThat(appointmentWithId.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(appointmentWithId.getObservations()).isEqualTo("nenhuma");
        assertThat(appointmentWithId.getCreatedAt()).isEqualTo(date);
        assertThat(appointmentWithId.getUpdatedAt()).isEqualTo(date);
    }

    @Test
    public void CreateAppointmentWithId_Fail_NullId() {
        Long appointmentId = null;
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.withPersistenceId(appointmentId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }

    @Test
    public void CreateAppointmentWithId_Fail_NegativeId() {
        Long appointmentId = -10L;
        OffsetDateTime date = OffsetDateTime.now();
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        Appointment appointment = new Appointment(1L, 2L, 3L, 4L, startAt, 180, AppointmentStatus.SCHEDULED, "nenhuma", date, date);

        var ex = assertThrows(DomainValidationException.class, () -> {
            appointment.withPersistenceId(appointmentId);
        });
        assertThat(ex.getMessage()).isEqualTo("Id inválido");
    }
}
