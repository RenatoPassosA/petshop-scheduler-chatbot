package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class AddPetServiceCommand {
    private String name;
    private BigDecimal price;
    private Integer duration;
    private Office canDo;

    public AddPetServiceCommand() {
    }

    public AddPetServiceCommand (String name, BigDecimal price, Integer duration, Office canDo){
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.canDo = canDo;
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

    public Office getCanDo() {
        return canDo;
    }
}


