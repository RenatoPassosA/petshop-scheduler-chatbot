package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.repository.PetRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.PetEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.PetMapper;

@Repository
public class PetRepositoryJpa implements PetRepository {
    private final PetMapper petMapper;
    private final PetEntityRepository petEntityRepository;

    public PetRepositoryJpa (PetMapper petMapper, PetEntityRepository petEntityRepository) {
        this.petMapper = petMapper;
        this.petEntityRepository = petEntityRepository;
    }

    @Override
    public Pet save (Pet pet) {
        PetEntity persistence = petMapper.toJPA(pet);
        persistence = petEntityRepository.save(persistence);
        pet = pet.withPersistenceId(persistence.getId());
        return (pet);
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return (petEntityRepository
            .findById(id)
            .map(petMapper::toDomain));
    }

    @Override
    public List<Pet> listByTutor(Long tutorId) {
        return (petEntityRepository
            .findAllByTutorId(tutorId)
            .stream()
            .map(petMapper::toDomain)
            .toList());
    }

    @Override
    public boolean existsByIdAndTutorId(Long petId, Long tutorId) {
        return (petEntityRepository.existsByIdAndTutorId(petId, tutorId));
    }

    @Override
    public void deleteById(Long petId) {
        petEntityRepository.deleteById(petId);
    }

    @Override
    public boolean existsById(Long id) {
        return (petEntityRepository.existsById(id));
    }

    @Override
    public List<Pet> getAll() {
        List<PetEntity> petEntities = petEntityRepository.findAll();
        return petEntities.stream()
                        .map(petEntity -> petMapper.toDomain(petEntity))  // Corrigido para petEntity
                        .collect(Collectors.toList());
    }


}
