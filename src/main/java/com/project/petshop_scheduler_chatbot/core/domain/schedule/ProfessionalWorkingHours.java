package com.project.petshop_scheduler_chatbot.core.domain.schedule;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;

public class ProfessionalWorkingHours {
    private Long professionalId;
    private DayOfWeek weekday;
    private OffsetDateTime open;
    private OffsetDateTime close;
    private OffsetDateTime breakStart;
    private OffsetDateTime breakEnd;

    public ProfessionalWorkingHours () {
    }

    public ProfessionalWorkingHours (Long professionalId, DayOfWeek weekday, OffsetDateTime open, OffsetDateTime close, OffsetDateTime breakStart, OffsetDateTime breakEnd) {
        basicValidations(professionalId, weekday, open, close, breakStart, breakEnd);
        this.professionalId = professionalId;
        this.weekday = weekday;
        this.open = open;
        this.close = close;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }

    private void    basicValidations(Long professionalId, DayOfWeek weekday, OffsetDateTime open, OffsetDateTime close, OffsetDateTime breakStart, OffsetDateTime breakEnd) {
        if (professionalId == null)
            throw new IllegalArgumentException("Id do profissional é obrigatório");
        if (weekday == null)
            throw new IllegalArgumentException("Dia é obrigatório");
        if (open == null)
            throw new IllegalArgumentException("Horário de inicio da jornada é obrigatório");
        if (close == null)
            throw new IllegalArgumentException("Horário de término da jornada é obrigatório");
        if (close.isBefore(open) || open.equals(close))
            throw new IllegalArgumentException("Horario de trabalho inválido");
        if (breakStart == null)
            throw new IllegalArgumentException("Horário do inicio da pausa é obrigatório");
        if (breakEnd == null)
            throw new IllegalArgumentException("Horário do final da pausa é obrigatório");
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public DayOfWeek getWeekday() {
        return weekday;
    }

    public OffsetDateTime getOpen() {
        return open;
    }

    public OffsetDateTime getClose() {
        return close;
    }

    public OffsetDateTime getBreakStart() {
        return breakStart;
    }

    public OffsetDateTime getBreakEnd() {
        return breakEnd;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setWeekday(DayOfWeek weekday) {
        this.weekday = weekday;
    }

    public void setOpen(OffsetDateTime open) {
        this.open = open;
    }

    public void setClose(OffsetDateTime close) {
        this.close = close;
    }
}
