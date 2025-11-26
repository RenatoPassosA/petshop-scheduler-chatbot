package com.project.petshop_scheduler_chatbot.adapters.web.dto.professional;

import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;

public class GetProfessionalResponse {
    private Long		                    id;
    private String			                name;
    private Office			                function;
    
    public GetProfessionalResponse(Long id, String name, Office function) {
        this.id = id;
        this.name = name;
        this.function = function;
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
    
    
}
