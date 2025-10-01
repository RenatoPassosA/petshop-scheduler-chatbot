package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class UpdatePetServiceCommand {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;

    public UpdatePetServiceCommand() {
    }

    public UpdatePetServiceCommand (Long id, String name, BigDecimal price, Integer duration) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
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

    public Integer getDuration() {
        return duration;
    }
}
