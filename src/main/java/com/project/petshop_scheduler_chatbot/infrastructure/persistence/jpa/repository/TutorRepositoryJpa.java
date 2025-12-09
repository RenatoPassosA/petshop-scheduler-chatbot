package com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.repository.TutorRepository;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.entity.TutorEntity;
import com.project.petshop_scheduler_chatbot.infrastructure.persistence.jpa.mapper.TutorMapper;

@Repository
public class TutorRepositoryJpa implements TutorRepository {
    private final TutorMapper tutorMapper;
    private final TutorEntityRepository tutorEntityRepository;

    public TutorRepositoryJpa (TutorMapper tutorMapper, TutorEntityRepository tutorEntityRepository) {
        this.tutorMapper = tutorMapper;
        this.tutorEntityRepository = tutorEntityRepository;
    }

    @Override
    public Tutor save (Tutor tutor) {
        TutorEntity persistence = tutorMapper.toJPA(tutor);
        persistence = tutorEntityRepository.save(persistence);
        tutor = tutor.withPersistenceId(persistence.getId());
        return (tutor);
    }

    @Override
    public Optional<Tutor> findById(Long id) {
        return (tutorEntityRepository
            .findById(id)         
            .map(tutorMapper::toDomain));

    }
    
    @Override
    public Optional<Tutor> findByPhone(PhoneNumber phone) {
        String phoneString = phone.value();
        return (tutorEntityRepository
                .findByPhoneNumber(phoneString)
                .map(tutorMapper::toDomain));
    }

    @Override
    public boolean existsByPhone(PhoneNumber phone) {
        String phoneString = phone.value();
        return (tutorEntityRepository.existsByPhoneNumber(phoneString));
    }

    @Override
    public boolean existsById(Long id) {
        return (tutorEntityRepository.existsById(id));
    }

    @Override
    public void deleteById(Long id) {
        tutorEntityRepository.deleteById(id);
    }

    @Override
    public List<Tutor> getAll() {
        List<TutorEntity> tutorEntities = tutorEntityRepository.findAll();
        return tutorEntities.stream()
                        .map(tutorEntity -> tutorMapper.toDomain(tutorEntity))
                        .collect(Collectors.toList());
    }

}
