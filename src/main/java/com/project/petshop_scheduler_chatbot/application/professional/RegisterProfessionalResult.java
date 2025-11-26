package com.project.petshop_scheduler_chatbot.application.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class RegisterProfessionalResult {
    private String			                name;
    private Office			                function;

    public RegisterProfessionalResult(String name, Office function) {
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

