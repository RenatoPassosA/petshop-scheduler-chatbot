package com.project.petshop_scheduler_chatbot.application.pet;

public class AddPetToTutorResult {
    final private Long petId;
    final private Long tutorId;
    final private String name;
    final private String observation;

    public AddPetToTutorResult(Long petId, Long tutorId, String name, String observation) {
        this.petId = petId;
        this.tutorId = tutorId;
        this.name = name;
        this.observation = observation;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getName() {
        return name;
    }

    public String getObservation() {
        return observation ;
    }
}
