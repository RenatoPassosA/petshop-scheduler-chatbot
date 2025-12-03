package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetEntity;

@Component
public class PetMapper {
    public PetEntity toJPA(Pet pet) {
        if (pet == null)
            throw new DomainValidationException("Dados de entrada invalidos");
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
        if (entity == null)
            throw new DomainValidationException("Dados de entrada invalidos");
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