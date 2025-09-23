package com.project.petshop_scheduler_chatbot.application.appointment;

public interface CancelAppointmentUseCase {
    CancelAppointmentResult execute (CancelAppointmentCommand command);
}
