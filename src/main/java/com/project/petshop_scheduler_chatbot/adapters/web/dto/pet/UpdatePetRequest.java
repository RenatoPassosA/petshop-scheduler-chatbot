package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

public class UpdatePetRequest {
    private String          observations;

    public UpdatePetRequest(String observations) {
        this.observations = observations;
    }
    
    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
