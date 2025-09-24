package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.schedule.ProfessionalTimeOff;

public interface ProfessionalTimeOffRepository {
    List<ProfessionalTimeOff> listByProfessionalBetween(Long professionalId, LocalDateTime start, LocalDateTime end);
    boolean isInTimeOff(Long professionalId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
