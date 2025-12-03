package com.project.petshop_scheduler_chatbot.application.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class AddProfessionalResult {
    private Long                              professionalId;
    private String			                name;
    private Office			                function;

    public AddProfessionalResult(Long professionalId, String name, Office function) {
        this.professionalId = professionalId;
        this.name = name;
        this.function = function;
    }

    public Long getProfessionalId() {
        return professionalId;
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

