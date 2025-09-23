package com.project.petshop_scheduler_chatbot.application.appointment;

public interface ConfirmAppointmentUseCase {
    ConfirmAppointmentResult execute(ConfirmAppointmentCommand command);
}
