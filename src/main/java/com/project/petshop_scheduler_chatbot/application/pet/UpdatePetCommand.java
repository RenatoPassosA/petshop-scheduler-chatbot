package com.project.petshop_scheduler_chatbot.application.pet;

public class UpdatePetCommand {
    private Long petId;
    private String          observation; 

    public UpdatePetCommand () {
    }

    public UpdatePetCommand (Long petId, String observation) {
        this.petId = petId;
        this.observation = observation;
    }

    public Long getPetId() {
        return petId;
    }

    public String getObservation() {
        return observation;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
