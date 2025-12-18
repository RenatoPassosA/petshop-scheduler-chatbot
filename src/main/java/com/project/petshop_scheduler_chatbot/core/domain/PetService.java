package com.project.petshop_scheduler_chatbot.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class PetService {
    private Long			id;
    private String			name;
    private BigDecimal  	price;
    private int			    duration;
    private Office          canDo;
    private OffsetDateTime	createdAt;
    private OffsetDateTime	updatedAt;

    public PetService() {
    }

    public PetService(String name, BigDecimal price, int duration, Office canDo, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (name == null)
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        name = name.trim();
        basicValidations(name, duration, canDo);
        this.name = name;
        this.price = normalizePrice(price); 
        this.duration = duration;
        this.canDo = canDo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private PetService(Long id, String name, BigDecimal price, int duration, Office canDo, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (name == null)
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        name = name.trim();
        basicValidations(name, duration, canDo);
        this.id = id;
        this.name = name;
        this.price = normalizePrice(price); 
        this.duration = duration;
        this.canDo = canDo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private void    basicValidations(String name, int duration, Office canDo) {

        int scheduleStep = 15;

        if (name.isBlank())
            throw new DomainValidationException("Nome do Serviço é obrigatório");
        if (duration < 30 || duration > 180)
            throw new DomainValidationException("Duração válida do serviço é obrigatória (entre 30 e 180 minutos)");
        if (duration % scheduleStep != 0)
            throw new DomainValidationException("Normalizar duração de serviço terminando em multiplos de 15");
        if (canDo == null)
            throw new DomainValidationException("Informar profissional capacitado");
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null)
            throw new DomainValidationException("Preço é obrigatório");

        BigDecimal normalized = price.setScale(2, RoundingMode.HALF_UP);

        if (normalized.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainValidationException("Preço deve ser maior ou igual a 0,00");

        return normalized;
    }

    public PetService withPersistenceId (Long id) {
        if (id == null || id < 0)
            throw new DomainValidationException("Id inválido");
        return new PetService(id, this.name, this.price, this.duration, this.canDo, this.createdAt, this.updatedAt);
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

    public Office getCanDo() {
        return canDo;
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

    public void setCandDo(Office canDo) {
        this.canDo = canDo;
    }
}
