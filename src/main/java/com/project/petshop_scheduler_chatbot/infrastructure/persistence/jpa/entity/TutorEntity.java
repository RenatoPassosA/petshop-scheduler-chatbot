package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.time.OffsetDateTime;
import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutor")
public class TutorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long			id;
    @Column(nullable = false)
    private String			name;
    @Column(nullable = false)
    private PhoneNumber		phoneNumber;
    @Column(nullable = false)
    private String			address;
    @Column(nullable = false)
    private List<Long>      petIds;
    @Column(nullable = false)
    private OffsetDateTime	createdAt;
    @Column(nullable = false)
    private OffsetDateTime	updatedAt;

    public TutorEntity () {
    }

    public TutorEntity (String name, PhoneNumber phoneNumber, String address, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;    
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
}
