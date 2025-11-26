package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.AddTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.AddtutorRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.GetTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.tutor.UpdateTutorRequest;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.UpdateTutorCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;

public class TutorMapper {
    public static AddTutorCommand toCommand(AddtutorRequest r) {
        return new AddTutorCommand(r.getName(),
                                r.getPhoneNumber(),
                                r.getAddress());
    }

    public static AddTutorResponse toResponse(AddTutorResult r) {
        return new AddTutorResponse(r.getName(),
                                r.getPhoneNumber(),
                                r.getAddress());
    }

    public static UpdateTutorCommand toCommand(UpdateTutorRequest r) {
        return new UpdateTutorCommand(r.getName(),
                                r.getPhoneNumber(),
                                r.getAddress());
    }

    public static GetTutorResponse toResponse(Tutor r) {
        return new GetTutorResponse(r.getName(),
                                r.getPhoneNumber(),
                                r.getAddress());
    }
}


