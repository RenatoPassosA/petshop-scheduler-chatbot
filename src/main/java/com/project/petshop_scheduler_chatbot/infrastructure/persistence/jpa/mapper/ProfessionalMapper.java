package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;

@Component
public class ProfessionalMapper {
  public ProfessionalEntity toJPA(Professional professional) {
    if (professional == null)
            throw new IllegalArgumentException("Dados de entrada invalidos");
    return new ProfessionalEntity(professional.getName(),
                                professional.getFunction(),
                                professional.getCreatedAt(),
                                professional.getUpdatedAt()
                                );

    }

    public Professional toDomain(ProfessionalEntity entity) {
        if (entity == null)
            throw new IllegalArgumentException("Dados de entrada invalidos");
        Professional professional = new Professional(entity.getName(),
                                                    entity.getFunction(),
                                                    entity.getCreatedAt(),
                                                    entity.getUpdatedAt()
                                                    );
        professional = professional.withPersistenceId(entity.getId());
        return (professional);
    }
}
