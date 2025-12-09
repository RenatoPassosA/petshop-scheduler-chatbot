package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

public class AddPetToTutorResponse {

    private Long            petId;
    private String			petName;
    private Long            tutorId;
    private String          tutorName;

    public AddPetToTutorResponse(Long petId, String petName, Long tutorId, String tutorName) {
        this.petId = petId;
        this.petName = petName;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
    }

    public Long getPetId() {
        return petId;
    }

    public String getPetName() {
        return petName;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setPetName(String name) {
        this.petName = name;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
}
