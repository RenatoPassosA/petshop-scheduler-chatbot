package com.project.petshop_scheduler_chatbot.adapters.web.dto.pet;

public class AddPetToTutorResponse {

    private String			name;
    private Long            tutorId;
    private String          tutorName;

    public AddPetToTutorResponse(String name, Long tutorId, String tutorName) {
        this.name = name;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
    }

    public String getName() {
        return name;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
}
