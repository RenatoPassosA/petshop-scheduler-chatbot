package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

import jakarta.validation.constraints.NotNull;

public class AddProfessionalRequest {
    @NotNull(message = "O nome é obrigatório")
    private String			                name;
    @NotNull(message = "O cargo é obrigatório")
    private Office			                function;

    public AddProfessionalRequest (String name, Office function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public Office getFunction() {
        return function;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFunction(Office function) {
        this.function = function;
    }
}
