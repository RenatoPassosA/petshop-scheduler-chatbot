package com.project.petshop_scheduler_chatbot.application.pet;

public class UpdatePetCommand {
    private Long            petId;
    private String          observations; 

    public UpdatePetCommand () {
    }

    public UpdatePetCommand (Long petId, String observation) {
        this.petId = petId;
        this.observations = observation;
    }

    public Long getPetId() {
        return petId;
    }

    public String getObservations() {
        return observations;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setObservations(String observation) {
        this.observations = observation;
    }
}
