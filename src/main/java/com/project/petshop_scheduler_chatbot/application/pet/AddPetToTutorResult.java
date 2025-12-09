package com.project.petshop_scheduler_chatbot.application.pet;

public class AddPetToTutorResult {
    final private Long petId;
    final private Long tutorId;
    final private String petName;
    final private String observation;

    public AddPetToTutorResult(Long petId, Long tutorId, String petName, String observation) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.petName = petName;
        this.observation = observation;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getPetName() {
        return petName;
    }

    public String getObservation() {
        return observation ;
    }
}
