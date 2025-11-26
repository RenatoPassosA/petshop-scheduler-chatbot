package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;

@Component
public class ProfessionalTimeOffMapper {

    public ProfessionalTimeOff toDomain(ProfessionalTimeOffEntity entity) {
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