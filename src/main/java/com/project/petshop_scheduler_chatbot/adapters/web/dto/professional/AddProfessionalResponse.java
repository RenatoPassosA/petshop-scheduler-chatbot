package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class AddProfessionalResponse {
    private String			                name;
    private Office			                function;

    public AddProfessionalResponse (String name, Office function) {
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
