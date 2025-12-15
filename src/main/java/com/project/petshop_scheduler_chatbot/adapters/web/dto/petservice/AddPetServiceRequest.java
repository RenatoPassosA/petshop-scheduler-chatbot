package com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice;

import java.math.BigDecimal;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AddPetServiceRequest {
    @NotNull(message = "O nome é obrigatório")
    private String			name;
    @NotNull(message = "O preço é obrigatório")
    @Positive
    private BigDecimal  	price;
    @NotNull(message = "A duração é obrigatória")
    @Positive
    private int			    duration;
    @NotNull(message = "O profissional capacitado é obrigatório")
    private Office canDo;

    public AddPetServiceRequest(String name, BigDecimal price, int duration, Office canDo) {
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

    public int getDuration() {
        return duration;
    }

    public Office getCanDo() {
        return canDo;
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

    public void setCanDo(Office canDo) {
        this.canDo = canDo;
    }
}
