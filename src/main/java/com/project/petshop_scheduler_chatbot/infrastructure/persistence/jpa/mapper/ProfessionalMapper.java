package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;

public class ProfessionalMapper {
  public ProfessionalEntity toJPA(Professional professional) {
        return new ProfessionalEntity(professional.getName(),
                                    professional.getFunction(),
                                    professional.getCreatedAt(),
                                    professional.getUpdatedAt()
                                    );

    }

    public Professional toDomain(ProfessionalEntity entity) {
        Professional professional = new Professional(entity.getName(),
                                                    entity.getFunction(),
                                                    entity.getCreatedAt(),
                                                    entity.getUpdatedAt()
                                                    );
        professional = professional.withPersistenceId(entity.getId());
        return (professional);
    }
}
