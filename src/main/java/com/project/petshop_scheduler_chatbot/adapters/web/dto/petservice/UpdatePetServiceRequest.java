package com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdatePetServiceRequest {
    @NotNull(message = "O preço é obrigatório")
    @Positive
    private BigDecimal  	price;
    @NotNull(message = "A duração é obrigatória")
    @Positive
    private int			    duration;

    public UpdatePetServiceRequest(BigDecimal price, int duration) {
        this.price = price;
        this.duration = duration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
