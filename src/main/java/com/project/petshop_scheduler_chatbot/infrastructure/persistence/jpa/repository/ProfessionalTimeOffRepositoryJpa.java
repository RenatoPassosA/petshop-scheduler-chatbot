package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.ProfessionalTimeOff;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalTimeOffRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalTimeOffEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.ProfessionalTimeOffMapper;

@Repository
public class ProfessionalTimeOffRepositoryJpa implements ProfessionalTimeOffRepository {
    private final ProfessionalTimeOffEntityRepository professionalTimeOffEntityRepository;
    private final ProfessionalEntityRepository professionalEntityRepository;
    private final ProfessionalTimeOffMapper professionalTimeOffMapper;

    public ProfessionalTimeOffRepositoryJpa(ProfessionalTimeOffEntityRepository professionalTimeOffEntityRepository, ProfessionalEntityRepository professionalEntityRepository, ProfessionalTimeOffMapper professionalTimeOffMapper) {
        this.professionalTimeOffEntityRepository = professionalTimeOffEntityRepository;
        this.professionalEntityRepository = professionalEntityRepository;
        this.professionalTimeOffMapper = professionalTimeOffMapper;
    }

    @Override
    public boolean existsOverlap(Long professionalId, OffsetDateTime start, OffsetDateTime end) {
        return (professionalTimeOffEntityRepository.
                existsByProfessional_IdAndStartAtLessThanAndEndAtGreaterThan(professionalId,
                                                                            end,
                                                                            start));
    }

    @Override
    public ProfessionalTimeOff save(ProfessionalTimeOff timeOff) {
        ProfessionalEntity professionalEntity = professionalEntityRepository.getReferenceById(timeOff.getProfessionalId());
        ProfessionalTimeOffEntity persistence = professionalTimeOffMapper.toJPA(timeOff, professionalEntity);
        persistence = professionalTimeOffEntityRepository.save(persistence);
        timeOff = timeOff.withPersistenceId(persistence.getId());
        return timeOff;
    }

    @Override
    public Optional<ProfessionalTimeOff> findById(Long id) {
        return (professionalTimeOffEntityRepository
            .findById(id)
            .map(professionalTimeOffMapper::toDomain));
    }

    @Override
    public boolean existsById(Long id) {
        return professionalTimeOffEntityRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        professionalTimeOffEntityRepository.deleteById(id);
    }

    @Override
    public List<ProfessionalTimeOff> findAllByProfessionalId(Long professionalId) {
        return professionalTimeOffEntityRepository.getAllByProfessional_Id(professionalId)
                .stream()
                .map(professionalTimeOffMapper::toDomain)
                .toList();
    }

}
