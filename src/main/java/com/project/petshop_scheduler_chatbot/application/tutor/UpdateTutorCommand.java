package com.project.petshop_scheduler_chatbot.application.tutor;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class UpdateTutorCommand {
    private String			name;
    private PhoneNumber		phoneNumber;
    private String			address;

    public UpdateTutorCommand () {
    }

    public UpdateTutorCommand (String name, PhoneNumber phoneNumber, String address) {
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
}
