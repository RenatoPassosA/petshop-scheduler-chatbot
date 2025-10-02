package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetServiceEntity;

public class PetServiceMapper {
    public PetServiceEntity toJPA(PetService petService) {
            return new PetServiceEntity(petService.getName(),
                                        petService.getPrice(),
                                        petService.getDuration(),
                                        petService.getCreatedAt(),
                                        petService.getUpdatedAt()
                                        );

    }

    public PetService toDomain(PetServiceEntity entity) {
        PetService petService = new PetService(entity.getName(),
                                            entity.getPrice(),
                                            entity.getDuration(),
                                            entity.getCreatedAt(),
                                            entity.getUpdatedAt()
                                            );
        petService = petService.withPersistenceId(entity.getId());
        return (petService);
    }
}
