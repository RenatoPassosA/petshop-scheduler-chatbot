package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.AddPetServiceRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.AddPetServiceResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.GetPetServiceResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.UpdatePetServiceRequest;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceResult;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;

public class PetServiceWebMapper {
     public static AddPetServiceCommand toCommand(AddPetServiceRequest request) {
        return new AddPetServiceCommand(request.getName(),
                                    request.getPrice(),
                                    request.getDuration());
     }


     public static AddPetServiceResponse toResponse(AddPetServiceResult result) {
        return new AddPetServiceResponse(result.getName(),
                                        result.getPrice(),
                                        result.getDuration());
     }

     public static UpdatePetServiceCommand toCommand(UpdatePetServiceRequest request) {
        return new UpdatePetServiceCommand(request.getPrice(),
                                        request.getDuration());
     }

     public static GetPetServiceResponse toResponse(PetService r) {
        return new GetPetServiceResponse(r.getName(),
                                        r.getPrice(),
                                        r.getDuration());
     }


}
