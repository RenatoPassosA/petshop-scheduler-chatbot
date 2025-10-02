package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetEntity;

public class PetMapper {
    public PetEntity toJPA(Pet pet) {
        return new PetEntity(pet.getName(),
                            pet.getGender(),
                            pet.getSize(),
                            pet.getBreed(),
                            pet.getTutorId(),
                            pet.getObservations(),
                            pet.getCreatedAt(),
                            pet.getUpdatedAt()
                            );

    }

    public Pet toDomain(PetEntity entity) {
        Pet pet = new Pet(entity.getName(),
                        entity.getGender(),
                        entity.getSize(),
                        entity.getBreed(),
                        entity.getTutorId(),
                        entity.getObservations(),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                        );
        pet = pet.withPersistenceId(entity.getId());
        return (pet);
    }
}