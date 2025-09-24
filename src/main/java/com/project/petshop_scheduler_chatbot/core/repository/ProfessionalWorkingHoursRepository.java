package com.project.petshop_scheduler_chatbot.core.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.schedule.ProfessionalWorkingHours;

public interface ProfessionalWorkingHoursRepository {
    List<ProfessionalWorkingHours> findByProfessionalId (Long professionalId);
    boolean existsWindow(Long professionalId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    // findWindowsByProfessionalAndDate(professionalId, date)
    // findNextAvailableWindows(professionalId, fromDateTime, limit)
}
