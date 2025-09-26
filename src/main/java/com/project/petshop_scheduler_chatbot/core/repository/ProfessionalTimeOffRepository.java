package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.OffsetDateTime;
import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.schedule.ProfessionalTimeOff;

public interface ProfessionalTimeOffRepository {
    List<ProfessionalTimeOff> listByProfessionalBetween(Long professionalId, OffsetDateTime start, OffsetDateTime end);
    boolean isInTimeOff(Long professionalId, OffsetDateTime startDateTime, OffsetDateTime endDateTime);
}
