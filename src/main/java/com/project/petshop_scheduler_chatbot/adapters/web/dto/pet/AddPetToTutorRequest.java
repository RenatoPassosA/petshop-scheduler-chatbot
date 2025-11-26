package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

public class AddPetToTutorRequest {
    private String			name;
    private Gender			gender;
    private PetSize		    size;
    private String			breed;
    private Long            tutorId;
    private String          observations;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public AddPetToTutorRequest(String name, Gender gender, PetSize size, String breed, Long tutorId, String observations, OffsetDateTime createdAt, OffsetDateTime updatedAt){
        this.name = name;
        this.gender = gender;
        this.size = size;
        this.breed = breed;
        this.tutorId = tutorId;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
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
        this.name = name;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setSize(PetSize size) {
        this.size = size;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
