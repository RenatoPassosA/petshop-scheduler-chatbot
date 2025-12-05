package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class Tutor {
    private Long			id;
    private String			name;
    private PhoneNumber		phoneNumber;
    private String			address;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public Tutor (String name, PhoneNumber phoneNumber, String address, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (name == null)
            throw new DomainValidationException("Nome do Tutor é obrigatório");
        if (address == null)
            throw new DomainValidationException("Endereço do Tutor é obrigatório");
        name = name.trim();
        address = address.trim();
        basicValidations(name, phoneNumber, address);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;    
    }

    private Tutor (Long id, String name, PhoneNumber phoneNumber, String address, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (name == null)
            throw new DomainValidationException("Nome do Tutor é obrigatório");
        if (address == null)
            throw new DomainValidationException("Endereço do Tutor é obrigatório");
        name = name.trim();
        address = address.trim();
        basicValidations(name, phoneNumber, address);
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;    
    }

    private void    basicValidations(String name, PhoneNumber phoneNumber, String address) {
        if (name.isBlank())
            throw new DomainValidationException("Nome do Tutor é obrigatório");
        if (phoneNumber == null)
            throw new DomainValidationException("Telefone do Tutor é obrigatório");
        if (address.isBlank())
            throw new DomainValidationException("Endereço do Tutor é obrigatório");
    }

    public Tutor withPersistenceId (Long id) {
        if (id == null || id < 0)
            throw new DomainValidationException("Id inválido");
        return new Tutor(id, this.name, this.phoneNumber, this.address, this.createdAt, this.updatedAt);
    }

    public Long getId() {
        return id;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
