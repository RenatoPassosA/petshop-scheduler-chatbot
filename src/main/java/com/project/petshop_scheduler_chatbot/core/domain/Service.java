package com.project.petshop_scheduler_chatbot.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Service {
    private Long			id;
    private String			name;
    private BigDecimal  	price;
    private int			    duration;
    private LocalDateTime	createdAt;
    private LocalDateTime	updatedAt;

    public Service() {
    }

    public Service(String name, BigDecimal price, int duration) {
        basicValidations(name, price, duration);
        this.name = name;
        this.price = normalizePrice(price); 
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void    basicValidations(String name, BigDecimal price, int duration) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do Serviço é obrigatório");
        if (duration < 30 || duration > 180)
            throw new IllegalArgumentException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null)
            throw new IllegalArgumentException("Preço é obrigatório");

        BigDecimal normalized = price.setScale(2, RoundingMode.HALF_UP);

        if (normalized.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço deve ser maior ou igual a 0,00");

        return normalized;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.updatedAt = LocalDateTime.now();
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.updatedAt = LocalDateTime.now();
        this.price = normalizePrice(price);
    }

    public void setDuration(int duration) {
        this.updatedAt = LocalDateTime.now();
        this.duration = duration;
    }
}
