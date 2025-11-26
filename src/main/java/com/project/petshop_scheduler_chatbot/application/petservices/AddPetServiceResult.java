package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class AddPetServiceResult {
    final private Long		    id;
    final private String		name;
    final private BigDecimal  	price;
    final private Integer		duration;
    
    public AddPetServiceResult (Long id, String name, BigDecimal price, Integer duration) {
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
