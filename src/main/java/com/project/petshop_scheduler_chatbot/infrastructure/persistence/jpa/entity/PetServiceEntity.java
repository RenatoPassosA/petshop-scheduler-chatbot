package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pet_service")
public class PetServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String			name;
    @Column(nullable = false)
    private BigDecimal  	price;
    @Column(nullable = false)
    private int			    duration;
    @Column(nullable = false)
    private Office          canDo;
    @Column(nullable = false)
    private OffsetDateTime	createdAt;
    @Column(nullable = false)
    private OffsetDateTime	updatedAt;


    public PetServiceEntity (){
    }

    public PetServiceEntity (String name, BigDecimal price, int duration, Office canDo, OffsetDateTime createdAt, OffsetDateTime updatedAt){
        this.name = name;
        this.price = price; 
        this.duration = duration;
        this.canDo = canDo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public Office getCanDo() {
        return canDo;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
