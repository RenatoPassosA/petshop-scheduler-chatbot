package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;

public class TimeOffMapper {

     public ProfessionalTimeOff toDomain(ProfessionalTimeOffEntity entity) {
        if (entity == null)
            return null;
        return new ProfessionalTimeOff(
            entity.getId(),
            entity.getProfessional().getId(),
            entity.getReason(),
            entity.getStartAt(),
            entity.getEndAt()
        );
     }

    public List<ProfessionalTimeOff> toDomainList(List<ProfessionalTimeOffEntity> entities) {
        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
}
