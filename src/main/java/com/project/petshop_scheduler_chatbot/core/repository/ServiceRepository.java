package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.List;
import java.util.Optional;

import com.project.petshop_scheduler_chatbot.core.domain.Service;

public interface ServiceRepository {
    Service save (Service service);
    Optional<Service> findById(Long id);
    List<Service> findByName(String name);
}
