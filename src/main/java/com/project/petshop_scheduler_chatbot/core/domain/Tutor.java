package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public class Tutor {
    private Long			id;
    private String			name;
    private PhoneNumber		phoneNumber;
    private String			address;
    private List<Long>      petIds;
    private LocalDateTime	createdAt;
    private LocalDateTime	updatedAt;

    public Tutor () {
    }

    public Tutor (String name, PhoneNumber phoneNumber, String address, List<Long> petIds) {
            basicValidations(name, phoneNumber, address);
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.petIds = (petIds == null) ? new ArrayList<>() : new ArrayList<>(petIds);
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();   
    }

    private void    basicValidations(String name, PhoneNumber phoneNumber, String address) {
            if (name == null || name.isBlank())
				throw new IllegalArgumentException("Nome do Tutor é obrigatório");
			if (phoneNumber == null)
				throw new IllegalArgumentException("Telefone do Tutor é obrigatório");
			if (address == null || address.isBlank())
				throw new IllegalArgumentException("Endereço do Tutor é obrigatório");
        }

    public void    addPet(Long petId) 
    {
        if (petId == null || petId <= 0)
            throw new IllegalArgumentException("Pet Inválido");
        if (petIds.contains(petId))
            throw new IllegalArgumentException("Pet já está associado a este Tutor");
        this.petIds.add(petId);
        this.updatedAt = LocalDateTime.now();
    }

    public void    removePet(Long petId)
    {
        if (petId == null || petId <= 0)
            throw new IllegalArgumentException("Pet Inválido");
        if (!petIds.contains(petId))
            throw new IllegalArgumentException("Pet não pertence a esse Tutor");
        this.petIds.remove(petId);
        this.updatedAt = LocalDateTime.now();
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

    public List<Long> getPetIds() {
        return Collections.unmodifiableList(petIds);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPhone(PhoneNumber phoneNumber) {
        this.updatedAt = LocalDateTime.now();
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.updatedAt = LocalDateTime.now();
        this.address = address;
    }

    

}
