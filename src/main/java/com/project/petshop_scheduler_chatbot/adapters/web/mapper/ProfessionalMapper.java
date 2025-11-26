package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddProfessionalRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddProfessionalResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.GetProfessionalResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.UpdateProfessionalRequest;
import com.project.petshop_scheduler_chatbot.application.professional.RegisterProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.RegisterProfessionalResult;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;

public class ProfessionalMapper {
    static public UpdateProfessionalCommand toCommand (UpdateProfessionalRequest r) {
        return new UpdateProfessionalCommand(r.getName(),
                                        r.getFunction());
    } 

    static public GetProfessionalResponse toResponse (Professional r) {
        return new GetProfessionalResponse(r.getId(),
                                        r.getName(),
                                        r.getFunction());
    }

    static public RegisterProfessionalCommand toCommand (AddProfessionalRequest r) {
        return new RegisterProfessionalCommand(r.getName(),
                                        r.getFunction());
    }

    static public AddProfessionalResponse toResponse (RegisterProfessionalResult r) {
        return new AddProfessionalResponse(r.getName(),
                                        r.getFunction());
    }
}

