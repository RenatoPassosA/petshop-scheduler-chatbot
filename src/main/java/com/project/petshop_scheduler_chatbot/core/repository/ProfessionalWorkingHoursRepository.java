package com.project.petshop_scheduler_chatbot.core.repository;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.schedule.ProfessionalWorkingHours;

public interface ProfessionalWorkingHoursRepository {
    List<ProfessionalWorkingHours> findByProfessionalId (Long professionalId);
}
