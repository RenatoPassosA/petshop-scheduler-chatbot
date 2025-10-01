package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class RegisterPetServiceCommand {
    private String name;
    private BigDecimal price;
    private Integer duration;

    public RegisterPetServiceCommand() {
    }

    public RegisterPetServiceCommand (String name, BigDecimal price, Integer duration){
        this.name = name;
        this.price = price;
        this.duration = duration;
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


