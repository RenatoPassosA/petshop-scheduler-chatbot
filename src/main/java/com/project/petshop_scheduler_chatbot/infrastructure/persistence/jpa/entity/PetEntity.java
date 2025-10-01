package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pet")
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long			id;
    @Column(nullable = false)
    private String			name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender			gender;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PetSize		    size;
    @Column(nullable = false)
    private String			breed;
    @Column(nullable = false)
    private Long            tutorId;
    @Column(nullable = true)
    private String          observations;
    @Column(nullable = false)
    private OffsetDateTime	createdAt;
    @Column(nullable = false)
    private OffsetDateTime	updatedAt;

    public PetEntity() {
    }

    public PetEntity(String name, Gender gender, PetSize size, String breed, Long tutorId, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt){
        this.name = name;
        this.gender = gender;
        this.size = size;
        this.breed = breed;
        this.tutorId = tutorId;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public PetSize getSize() {
        return size;
    }

    public String getBreed() {
        return breed;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getObservations() {
        return observations;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    

}
