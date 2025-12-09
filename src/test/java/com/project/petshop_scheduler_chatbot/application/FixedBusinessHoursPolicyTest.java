package com.project.petshop_scheduler_chatbot.application;

import com.project.petshop_scheduler_chatbot.application.policy.FixedBusinessHoursPolicy;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FixedBusinessHoursPolicyTest {

    private final FixedBusinessHoursPolicy businessHoursPolicy = new FixedBusinessHoursPolicy();

    @Test
    public void testFits_WeekdayWithinBusinessHours() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-08T12:30:00Z"); // Segunda-feira 12h30
        OffsetDateTime end = OffsetDateTime.parse("2025-12-08T14:00:00Z"); // Segunda-feira 14h00
        assertTrue(businessHoursPolicy.fits(start, end), "O agendamento deve ser aceito durante o horário comercial.");
    }

    @Test
    public void testFits_WeekdayOutsideBusinessHours_StartBefore() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-08T08:30:00Z"); // Segunda-feira 8h30
        OffsetDateTime end = OffsetDateTime.parse("2025-12-08T09:00:00Z"); // Segunda-feira 9h00
        assertFalse(businessHoursPolicy.fits(start, end), "O agendamento antes das 9h00 deve ser recusado.");
    }

    @Test
    public void testFits_WeekdayOutsideBusinessHours_EndAfter() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-08T17:30:00Z"); // Segunda-feira 17h30
        OffsetDateTime end = OffsetDateTime.parse("2025-12-08T18:30:00Z"); // Segunda-feira 18h30
        assertFalse(businessHoursPolicy.fits(start, end), "O agendamento após as 18h00 deve ser recusado.");
    }

    @Test
    public void testFits_SaturdayWithinBusinessHours() {
        OffsetDateTime start = OffsetDateTime.parse("2025-01-06T09:00:00Z"); // Sábado 9h00
        OffsetDateTime end = OffsetDateTime.parse("2025-01-06T12:00:00Z"); // Sábado 12h00
        assertTrue(businessHoursPolicy.fits(start, end), "O agendamento deve ser aceito no sábado das 8h 14h.");
    }

    @Test
    public void testFits_SaturdayOutsideBusinessHours() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-06T14:30:00Z"); // Sábado 14h30
        OffsetDateTime end = OffsetDateTime.parse("2025-12-06T15:00:00Z"); // Sábado 15h00
        assertFalse(businessHoursPolicy.fits(start, end), "O agendamento após as 14h00 no sábado deve ser recusado.");
    }

    @Test
    public void testFits_Sunday() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-07T09:30:00Z"); // Domingo 9h30
        OffsetDateTime end = OffsetDateTime.parse("2025-12-07T10:00:00Z"); // Domingo 10h00
        assertFalse(businessHoursPolicy.fits(start, end), "O agendamento no domingo deve ser recusado.");
    }

    @Test
    public void testFits_StartAfterEnd() {
        OffsetDateTime start = OffsetDateTime.parse("2025-12-08T10:00:00Z"); // Segunda-feira 10h00
        OffsetDateTime end = OffsetDateTime.parse("2025-12-08T09:00:00Z"); // Segunda-feira 9h00
        assertFalse(businessHoursPolicy.fits(start, end), "O horário de início não pode ser posterior ao de término.");
    }

    @Test
    public void testFits_SameDayBoundary() {
        OffsetDateTime start = OffsetDateTime.parse("2025-01-05T18:00:00Z"); // Segunda-feira 18h00
        OffsetDateTime end = OffsetDateTime.parse("2025-01-05T18:30:00Z"); // Segunda-feira 18h30
        assertFalse(businessHoursPolicy.fits(start, end), "O agendamento não pode ultrapassar as 18h00.");
    }
}
