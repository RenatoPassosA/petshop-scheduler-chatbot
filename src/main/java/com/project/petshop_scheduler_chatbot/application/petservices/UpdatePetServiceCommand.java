package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class UpdatePetServiceCommand {
    private String name;
    private BigDecimal price;
    private Integer duration;

    public UpdatePetServiceCommand() {
    }

    public UpdatePetServiceCommand (BigDecimal price, Integer duration) {
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
