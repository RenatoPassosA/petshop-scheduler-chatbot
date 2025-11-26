package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetServiceEntity;

@Component
public class PetServiceMapper {
    public PetServiceEntity toJPA(PetService petService) {
        if (petService == null)
            throw new IllegalArgumentException("Dados de entrada invalidos");
        return new PetServiceEntity(petService.getName(),
                                    petService.getPrice(),
                                    petService.getDuration(),
                                    petService.getCreatedAt(),
                                    petService.getUpdatedAt()
                                    );

    }

    public PetService toDomain(PetServiceEntity entity) {
        if (entity == null)
            throw new IllegalArgumentException("Dados de entrada invalidos");
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
