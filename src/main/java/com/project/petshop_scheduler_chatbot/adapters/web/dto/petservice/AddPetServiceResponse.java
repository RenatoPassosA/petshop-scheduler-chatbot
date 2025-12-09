package com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice;

import java.math.BigDecimal;

public class AddPetServiceResponse {
    private Long    		id;
    private String			name;
    private BigDecimal  	price;
    private int			    duration;

    public AddPetServiceResponse(Long id, String name, BigDecimal price, int duration) {
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

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
