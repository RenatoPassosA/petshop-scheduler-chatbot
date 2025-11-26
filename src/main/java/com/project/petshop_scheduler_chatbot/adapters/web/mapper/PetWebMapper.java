package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.AddPetToTutorRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.AddPetToTutorResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.GetPetResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.pet.UpdatePetRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.petservice.GetPetServiceResponse;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorResult;
import com.project.petshop_scheduler_chatbot.application.pet.UpdatePetCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;

public class PetWebMapper {
    public static AddPetToTutorCommand toCommand(AddPetToTutorRequest r) {
        return new AddPetToTutorCommand(r.getName(),
                                    r.getGender(),
                                    r.getSize(),
                                    r.getBreed(),
                                    r.getTutorId(),
                                    r.getObservations());
    }

    public static AddPetToTutorResponse toResponse(AddPetToTutorResult r, String tutorName) {
        return new AddPetToTutorResponse(r.getName(),
                                        r.getTutorId(),
                                        tutorName);
    }


    public static UpdatePetCommand toCommand(UpdatePetRequest request) {
        return new UpdatePetCommand(request.getPetId(), request.getObservations());
    }

    public static GetPetResponse toResponse(Pet pet, String tutorName) {
        return new GetPetResponse(pet.getName(),
                                tutorName,
                                pet.getGender(),
                                pet.getSize(),
                                pet.getBreed(),
                                pet.getTutorId(),
                                pet.getObservations());
    } 

    public static GetPetServiceResponse toResponse(PetService petService) {
        return new GetPetServiceResponse(petService.getName(),
                                        petService.getPrice(),
                                        petService.getDuration());
    }
}
