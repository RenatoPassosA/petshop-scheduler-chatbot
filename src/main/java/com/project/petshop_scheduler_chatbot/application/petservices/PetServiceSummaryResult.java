package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;

public class PetServiceSummaryResult {
    final private Long		    id;
    final private String		name;
    final private BigDecimal  	price;
    final private int		    duration;
    
    public PetServiceSummaryResult (Long id, String name, BigDecimal price, int duration) {
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

    public int getDuration() {
        return duration;
    }
}
