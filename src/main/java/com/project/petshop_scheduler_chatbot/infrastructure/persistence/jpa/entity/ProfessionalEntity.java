package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professional")
public class ProfessionalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long			id;
    @Column(nullable = false)
    private String			name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Office			function;
    @Column(nullable = false)
    private OffsetDateTime	createdAt;
    @Column(nullable = false)
    private OffsetDateTime	updatedAt;

    public ProfessionalEntity () {
    }

    public ProfessionalEntity (String name, Office function, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.name = name;
        this.function = function;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Office getFunction() {
        return function;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
