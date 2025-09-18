package com.project.petshop_scheduler_chatbot.core.domain;

import java.time.LocalDateTime;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class Professional {
    private Long			id;
    private String			name;
    private Office			function;
    private LocalDateTime	createdAt;
    private LocalDateTime	updatedAt;

    public Professional () {
    }

    public Professional (String name, Office function) {
        basicValidations(name, function);
        this.name = name;
        this.function = function;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void    basicValidations(String name, Office function) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do Profissional é obrigatório");
        if (function == null)
            throw new IllegalArgumentException("Função é obrigatória");
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

    public void setFunction(Office function) {
        this.updatedAt = LocalDateTime.now();
        this.function = function;
    }
}
