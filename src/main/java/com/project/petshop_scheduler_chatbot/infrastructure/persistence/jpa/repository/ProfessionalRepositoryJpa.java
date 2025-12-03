package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.repository.ProfessionalRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.ProfessionalEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.ProfessionalMapper;

@Repository
public class ProfessionalRepositoryJpa implements ProfessionalRepository {
    private final ProfessionalMapper professionalMapper;
    private final ProfessionalEntityRepository professionalEntityRepository;

    public ProfessionalRepositoryJpa(ProfessionalMapper professionalMapper, ProfessionalEntityRepository professionalEntityRepository) {
        this.professionalMapper = professionalMapper;
        this.professionalEntityRepository = professionalEntityRepository;
    }

    @Override
    public Professional save (Professional professional) {
        ProfessionalEntity persistence = professionalMapper.toJPA(professional);
        persistence = professionalEntityRepository.save(persistence);
        professional = professional.withPersistenceId(persistence.getId());
        return (professional);
    }

    @Override
    public Optional<Professional> findById(Long id) {
        return (professionalEntityRepository
            .findById(id)
            .map(professionalMapper::toDomain));
    }

    @Override
    public boolean existsById(Long id) {
        return (professionalEntityRepository.existsById(id));
    }

    @Override
    public void deleteById(Long id) {
        professionalEntityRepository.deleteById(id);
    }

    @Override
    public List<Professional> getAll() {
        List<ProfessionalEntity> professionalEntities = professionalEntityRepository.findAll();
        return professionalEntities.stream()
                        .map(professionalEntity -> professionalMapper.toDomain(professionalEntity))
                        .collect(Collectors.toList());
    }
}
