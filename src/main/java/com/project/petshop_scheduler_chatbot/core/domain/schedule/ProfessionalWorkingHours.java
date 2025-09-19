package com.project.petshop_scheduler_chatbot.core.domain.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class ProfessionalWorkingHours {
    private Long professionalId;
    private DayOfWeek weekday;
    private LocalTime open;
    private LocalTime close;

    public ProfessionalWorkingHours () {
    }

    public ProfessionalWorkingHours (Long professionalId, DayOfWeek weekday, LocalTime open, LocalTime close) {
        basicValidations(professionalId, weekday, open, close);
        this.professionalId = professionalId;
        this.weekday = weekday;
        this.open = open;
        this.close = close;
    }

    private void    basicValidations(Long professionalId, DayOfWeek weekday, LocalTime open, LocalTime close) {
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
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public DayOfWeek getWeekday() {
        return weekday;
    }

    public LocalTime getOpen() {
        return open;
    }

    public LocalTime getClose() {
        return close;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public void setWeekday(DayOfWeek weekday) {
        this.weekday = weekday;
    }

    public void setOpen(LocalTime open) {
        this.open = open;
    }

    public void setClose(LocalTime close) {
        this.close = close;
    }
}
