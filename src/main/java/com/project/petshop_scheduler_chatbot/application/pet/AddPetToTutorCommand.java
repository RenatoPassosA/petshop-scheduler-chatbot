package com.project.petshop_scheduler_chatbot.application.pet;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

public class AddPetToTutorCommand {
    private String			name;
    private Gender			gender;
    private PetSize		    size;
    private String			breed;
    private Long            tutorId;
    private String          observation; 

    public AddPetToTutorCommand () {
    }

    public AddPetToTutorCommand (String name, Gender gender, PetSize size, String breed, Long tutorId, String observation) {
        this.name = name;
        this.gender = gender;
        this.size = size;
        this.breed = breed;
        this.tutorId = tutorId;
        this.observation = observation;
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

    public String getObservation() {
        return observation;
    }
}
