package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class AddPetServiceResult {
    final private Long		    id;
    final private String		name;
    final private BigDecimal  	price;
    final private Integer		duration;
    final private String        canDo;
    
    public AddPetServiceResult (Long id, String name, BigDecimal price, Integer duration, String canDo) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.canDo = canDo;
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

    public String getCanDo() {
        return canDo;
    }
}
