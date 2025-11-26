package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;

@Repository
public class ProfessionalTimeOffRepositoryJpa implements ProfessionalTimeOffRepository {
    private final ProfessionalTimeOffEntityRepository professionalTimeOffEntityRepository;
    private final ProfessionalEntityRepository professionalEntityRepository;

    public ProfessionalTimeOffRepositoryJpa(ProfessionalTimeOffEntityRepository professionalTimeOffEntityRepository, ProfessionalEntityRepository professionalEntityRepository) {
        this.professionalTimeOffEntityRepository = professionalTimeOffEntityRepository;
        this.professionalEntityRepository = professionalEntityRepository;
    }

    @Override
    public boolean existsOverlap(Long professionalId, OffsetDateTime start, OffsetDateTime end) {
        return (professionalTimeOffEntityRepository.
                existsByProfessional_IdAndStartAtLessThanAndEndAtGreaterThan(professionalId,
                                                                            end,
                                                                            start));
    }

    @Override
    public void save(Long professionalId, OffsetDateTime start, OffsetDateTime end, String reason, OffsetDateTime createdAt) {
        ProfessionalEntity entity = professionalEntityRepository.getReferenceById(professionalId);
        ProfessionalTimeOffEntity timeOffEntity = new ProfessionalTimeOffEntity(reason,
                                                                                start,
                                                                                end,
                                                                                createdAt);
        timeOffEntity.setProfessional(entity);
        professionalTimeOffEntityRepository.save(timeOffEntity);
    }

    @Override
    public boolean existsById(Long id) {
        return professionalTimeOffEntityRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        professionalTimeOffEntityRepository.deleteById(id);
    }

}
