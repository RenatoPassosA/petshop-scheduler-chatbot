package com.project.petshop_scheduler_chatbot.application.professional;

public interface TimeOffUseCase {
    AddTimeOffResult execute (AddTimeOffCommand timeOff);
    void delete(Long professionalId, Long timeOffId);
}
