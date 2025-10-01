package com.project.petshop_scheduler_chatbot.application.petservices;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PetServiceSummaryResult {
    final private Long		    id;
    final private String		name;
    final private BigDecimal  	price;
    final private Integer		 duration;
    final private OffsetDateTime createdAt;
    final private OffsetDateTime updatedAt;
    
    public PetServiceSummaryResult (Long id, String name, BigDecimal price, Integer duration, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

}
