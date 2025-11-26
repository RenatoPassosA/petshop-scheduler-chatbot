package com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice;

import java.math.BigDecimal;

public class GetPetServiceResponse {
    private String			name;
    private BigDecimal  	price;
    private int			    duration;

    public GetPetServiceResponse(String name, BigDecimal price, int duration) {
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
