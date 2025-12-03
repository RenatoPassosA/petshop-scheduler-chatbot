package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddTimeOffRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.professional.AddTimeOffResponse;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.application.professional.TimeOffListResult;
import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;

public class TimeOffWebMapper {

    public static TimeOffListResult toTimeOffListResult(ProfessionalTimeOff timeOff) {
        return new TimeOffListResult(
            timeOff.getId(),
            timeOff.getReason(),
            timeOff.getStartAt(),
            timeOff.getEndAt()
        );
    }

    static public AddTimeOffCommand toCommand(Long id, AddTimeOffRequest request) {
        return new AddTimeOffCommand(id,
                                    request.getReason(),
                                    request.getStartAt(),
                                    request.getEndAt());
    }

    static public AddTimeOffResponse toResponse(AddTimeOffResult result) {
        return new AddTimeOffResponse(result.getProfessionalId(), 
                                    result.getReason(),
                                    result.getStartAt(),
                                    result.getEndAt());
    }
}
