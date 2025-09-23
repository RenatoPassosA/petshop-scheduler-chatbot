package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

public interface TutorRepository {
    Tutor save (Tutor tutor);
    Optional<Tutor> findById(Long id);
    Optional<Tutor> findByPhone(PhoneNumber phone); 
    boolean existsByPhone(PhoneNumber phone);
    boolean existsById(Long id);
}
