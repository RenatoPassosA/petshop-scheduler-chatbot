package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.TutorEntity;

public class TutorMapper {
    public TutorEntity toJPA(Tutor tutor) {
        return new TutorEntity(tutor.getName(),
                        tutor.getPhoneNumber().toString(),
                        tutor.getAddress(),
                        tutor.getCreatedAt(),
                        tutor.getUpdatedAt()
                        );

    }

    public Tutor toDomain(TutorEntity entity) {
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
