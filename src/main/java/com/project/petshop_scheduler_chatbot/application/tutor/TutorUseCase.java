package com.project.petshop_scheduler_chatbot.application.tutor;

import java.util.List;

import com.project.petshop_scheduler_chatbot.core.domain.Tutor;

public interface TutorUseCase {
    AddTutorResult execute (AddTutorCommand tutor);
    Tutor getTutor(Long id);
    List<Tutor> getAll();
    void update(Long id, UpdateTutorCommand command);
    void delete(Long id);
}
