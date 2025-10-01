package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professional_working_hours")
public class ProfessionalWorkingHoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long professionalId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek weekday;
    @Column(nullable = false)
    private LocalTime open;
    @Column(nullable = false)
    private LocalTime close;
    @Column
    private LocalTime breakStart;
    @Column
    private LocalTime breakEnd;

    public ProfessionalWorkingHoursEntity () {
    }

    public ProfessionalWorkingHoursEntity (Long id, Long professionalId, DayOfWeek weekday, LocalTime open, LocalTime close, LocalTime breakStart, LocalTime breakEnd) {
        this.id = id;
        this.professionalId = professionalId;
        this.weekday = weekday;
        this.open = open;
        this.close = close;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }

    public Long getId() {
        return id;
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

    public LocalTime getBreakStart() {
        return breakStart;
    }

    public LocalTime getBreakEnd() {
        return breakEnd;
    }
}