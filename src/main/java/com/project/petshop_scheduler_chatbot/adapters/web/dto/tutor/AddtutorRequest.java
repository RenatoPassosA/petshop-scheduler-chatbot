package com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

import jakarta.validation.constraints.NotNull;

public class AddtutorRequest {
    @NotNull(message = "O nome é obrigatório")
    private String			name;
    @NotNull(message = "O telefone é obrigatório")
    private PhoneNumber		phoneNumber;
    @NotNull(message = "O endereço é obrigatório")
    private String			address;
    
    public AddtutorRequest(String name, PhoneNumber phoneNumber, String address) {
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
