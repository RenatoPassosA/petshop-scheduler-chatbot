package com.project.petshop_scheduler_chatbot.application.tutor;

public class RegisterTutorResult {
    final private Long tutorId;
    final private String name;
    final private String phoneNumber;

    public RegisterTutorResult(Long tutorId, String name, String phoneNumber) {
        this.tutorId = tutorId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
