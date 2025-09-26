package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

public class Pet {
    private Long			id;
    private String			name;
    private Gender			gender;
    private PetSize		    size;
    private String			breed;
    private Long            tutorId;
    private String          observations;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public Pet(){   
    }

    public Pet(String name, Gender gender, PetSize size, String breed, Long tutorId, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt){
        basicValidations(name, gender, size, breed, tutorId, observations);
        this.name = name;
        this.gender = gender;
        this.size = size;
        this.breed = breed;
        this.tutorId = tutorId;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private void    basicValidations(String name, Gender gender, PetSize size, String breed, Long tutorId, String observations) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do pet é obrigatório");
        if (gender == null)
            throw new IllegalArgumentException("Sexo do pet deve ser M ou F");
        if (size == null)
            throw new IllegalArgumentException("Porte do pet é obrigatório");
        if (breed == null || breed.isBlank())
            throw new IllegalArgumentException("Raça do pet é obrigatória");
        if (tutorId == null || tutorId <= 0)
            throw new IllegalArgumentException("Inserir tutor válido");
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

    public void setName(String name) {
        this.updatedAt = OffsetDateTime.now();
        this.name = name;
    }

    public void setGender(Gender gender) {
        this.updatedAt = OffsetDateTime.now();
        this.gender = gender;
    }

    public void setSize(PetSize size) {
        this.updatedAt = OffsetDateTime.now();
        this.size = size;
    }

    public void setBreed(String breed) {
        this.updatedAt = OffsetDateTime.now();
        this.breed = breed;
    }

    public void setTutorId(Long tutorId) {
        this.updatedAt = OffsetDateTime.now();
        this.tutorId = tutorId;
    }

    public void setObservations(String observations) {
        this.updatedAt = OffsetDateTime.now();
        this.observations = observations;
    }
}
