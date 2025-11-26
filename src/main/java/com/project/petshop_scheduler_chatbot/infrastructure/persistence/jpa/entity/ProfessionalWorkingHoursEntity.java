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
    private LocalTime startAt;
    @Column(nullable = false)
    private LocalTime endAt;

    public ProfessionalWorkingHoursEntity () {
    }

    public ProfessionalWorkingHoursEntity (Long id, Long professionalId, DayOfWeek weekday, LocalTime startAt, LocalTime endAt) {
        this.id = id;
        this.professionalId = professionalId;
        this.weekday = weekday;
        this.startAt = startAt;
        this.endAt = endAt;
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

    public LocalTime getstartAt() {
        return startAt;
    }

    public LocalTime getendAt() {
        return endAt;
    }
}