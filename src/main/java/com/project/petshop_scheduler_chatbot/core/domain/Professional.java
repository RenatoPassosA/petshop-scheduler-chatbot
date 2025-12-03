package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.OffsetDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class Professional {
    private Long		                    id;
    private String			                name;
    private Office			                function;
    private OffsetDateTime	                createdAt;
    private OffsetDateTime	                updatedAt;

    public Professional () {
    }

    public Professional (String name, Office function, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(name, function);
        this.name = name;
        this.function = function;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private Professional (Long id, String name, Office function, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        basicValidations(name, function);
        this.id = id;
        this.name = name;
        this.function = function;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;  
    }

    private void    basicValidations(String name, Office function) {
        if (name == null || name.isBlank())
            throw new DomainValidationException("Nome do Profissional é obrigatório");
        if (function == null)
            throw new DomainValidationException("Função é obrigatória");
    }

    public Professional withPersistenceId (Long id) {
        if (id == null || id < 0)
            throw new DomainValidationException("Id inválido");
        return new Professional(id, this.name, this.function, this.createdAt, this.updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Office getFunction() {
        return function;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFunction(Office function) {
        this.function = function;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}
