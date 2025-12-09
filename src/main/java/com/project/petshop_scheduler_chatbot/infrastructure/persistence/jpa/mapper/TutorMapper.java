package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.TutorEntity;

@Component
public class TutorMapper {
    public TutorEntity toJPA(Tutor tutor) {
        if (tutor == null)
            throw new DomainValidationException("Dados de entrada invalidos");
        return new TutorEntity(tutor.getName(),
                        tutor.getPhoneNumber().value(),
                        tutor.getAddress(),
                        tutor.getCreatedAt(),
                        tutor.getUpdatedAt()
                        );

    }

    public Tutor toDomain(TutorEntity entity) {
        if (entity == null)
            throw new DomainValidationException("Dados de entrada invalidos");
        Tutor tutor = new Tutor(entity.getName(),
                                new PhoneNumber(entity.getPhoneNumber()),
                                entity.getAddress(),
                                entity.getCreatedAt(),
                                entity.getUpdatedAt()
                                );
        tutor = tutor.withPersistenceId(entity.getId());
        return (tutor);
    }
}
