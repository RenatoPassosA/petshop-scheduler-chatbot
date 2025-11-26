package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

public class UpdatePetRequest {
    private Long            petId;
    private String          observations;

    public UpdatePetRequest(Long petId, String observations) {
        this.petId = petId;
        this.observations = observations;
    }

    public Long getPetId() {
        return petId;
    }
    
    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
