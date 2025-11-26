package com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class GetTutorResponse {
    private String			name;
    private PhoneNumber 	phoneNumber;
    private String			address;
    
    public GetTutorResponse(String name, PhoneNumber phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}