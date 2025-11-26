package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

import jakarta.validation.constraints.NotNull;

public class UpdateProfessionalRequest {
    @NotNull(message = "Nome é obrigatório")
    private String                          name;
    @NotNull(message = "Cargo é obrigatório")
    private Office			                function;

    public UpdateProfessionalRequest(String name, Office function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public Office getFunction() {
        return function;
    }
}
