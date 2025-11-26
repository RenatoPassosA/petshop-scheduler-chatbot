package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.ServiceNotFoundException;
import com.project.petshop_scheduler_chatbot.core.repository.PetServiceRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetServiceEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.PetServiceMapper;

@Repository
public class PetServiceRepositoryJpa implements PetServiceRepository {
    private final PetServiceMapper petServiceMapper;
    private final PetServiceEntityRepository petServiceEntityRepository;

    public PetServiceRepositoryJpa (PetServiceMapper petServiceMapper, PetServiceEntityRepository petServiceEntityRepository) {
        this.petServiceMapper = petServiceMapper;
        this.petServiceEntityRepository = petServiceEntityRepository;
    }

    @Override
    public PetService save (PetService service) {
        PetServiceEntity persistence = petServiceMapper.toJPA(service);
        persistence = petServiceEntityRepository.save(persistence);
        service = service.withPersistenceId(persistence.getId());
        return (service);
    }

    @Override
    public Optional<PetService> findById(Long id) {
        return (petServiceEntityRepository
            .findById(id)
            .map(petServiceMapper::toDomain));
    }

    @Override
    public boolean existsById(Long id) {
        return (petServiceEntityRepository.existsById(id));
    }

    @Override
    public List<PetService> findByName(String name) {
        return (petServiceEntityRepository
            .findByName(name)
            .stream()
            .map(petServiceMapper::toDomain)
            .toList());
    }

    @Override
    public List<PetService> getAll() {
        return (petServiceEntityRepository.findAll()
            .stream()
            .map(petServiceMapper::toDomain)
            .toList());
    }

    @Override
    public int durationById(Long id) {
        if (id == null || id <= 0)
            throw new DomainValidationException("Id inválido");
        return findById(id)
        .map(PetService::getDuration)
        .orElseThrow(() -> new ServiceNotFoundException("Serviço não encontrado"));
    }

    @Override
    public void deleteById(Long id) {
        petServiceEntityRepository.deleteById(id);
    }    
}
