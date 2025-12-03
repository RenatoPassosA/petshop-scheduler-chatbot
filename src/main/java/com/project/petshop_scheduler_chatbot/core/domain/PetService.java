package com.project.petshop_scheduler_chatbot.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;

public class PetService {
    private Long			id;
    private String			name;
    private BigDecimal  	price;
    private int			    duration;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public PetService() {
    }

    public PetService(String name, BigDecimal price, int duration, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(name, duration);
        this.name = name;
        this.price = normalizePrice(price); 
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private PetService(Long id, String name, BigDecimal price, int duration, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(name, duration);
        this.name = name;
        this.price = normalizePrice(price); 
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private void    basicValidations(String name, int duration) {

        int scheduleStep = 15;

        if (name == null || name.isBlank())
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        if (duration < 30 || duration > 180)
            throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
        if (duration % scheduleStep != 0)
            throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null)
            throw new DomainValidationException("Preço é obrigatório");

        BigDecimal normalized = price.setScale(2, RoundingMode.HALF_UP);

        if (normalized.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainValidationException("Preço deve ser maior ou igual a 0,00");

        return normalized;
    }

    public void updateInfos(String newName, BigDecimal newPrice, Integer newDuration, OffsetDateTime nowUtc) {

        int scheduleStep = 15;

        if (newName != null) {
            if (name == null || name.isBlank() || name.trim().isEmpty())
                throw new DomainValidationException("Nome do Serviço é obrigatório");
            this.name = name.trim();
        }
        if (newPrice != null) {
            this.price = normalizePrice(newPrice);
        }
        if (newDuration != null) {
            if (duration < 30 || duration > 180)
                throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
            if (duration % scheduleStep != 0)
                throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
            this.duration = newDuration;
        }
        this.updatedAt = nowUtc;
    }

    public PetService withPersistenceId (Long id) {
        if (id == null || id < 0)
            throw new DomainValidationException("Id inválido");
        return new PetService(id, this.name, this.price, this.duration, this.createdAt, this.updatedAt);
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.updatedAt = OffsetDateTime.now();
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.updatedAt = OffsetDateTime.now();
        this.price = normalizePrice(price);
    }

    public void setDuration(int duration) {
        this.updatedAt = OffsetDateTime.now();
        this.duration = duration;
    }
}
