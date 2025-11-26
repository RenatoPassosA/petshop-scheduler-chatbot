package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

public class GetPetResponse {
    private String			name;
    private String          tutorName;
    private Gender			gender;
    private PetSize		    size;
    private String			breed;
    private Long            tutorId;
    private String          observations;
    
    public GetPetResponse(String name, String tutorName, Gender gender, PetSize size, String breed, Long tutorId,
            String observations) {
        this.name = name;
        this.tutorName = tutorName;
        this.gender = gender;
        this.size = size;
        this.breed = breed;
        this.tutorId = tutorId;
        this.observations = observations;
    }
    public String getName() {
        return name;
    }
    public String getTutorName() {
        return tutorName;
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
    public void setName(String name) {
        this.name = name;
    }
    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
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
    
    
}
