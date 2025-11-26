package com.project.petshop_scheduler_chatbot.application.tutor;


public class AddTutorResult {
    final private Long tutorId;
    final private String name;
    final private String phoneNumber;
    final private String address;

    public AddTutorResult(Long tutorId, String name, String phoneNumber, String address) {
        this.tutorId = tutorId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
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

    public String getAddress() {
        return address;
    }
}
