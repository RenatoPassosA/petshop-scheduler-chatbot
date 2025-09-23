package com.project.petshop_scheduler_chatbot.application.appointment;

public interface RescheduleAppointmentUseCase {
    RescheduleAppointmentResult execute (RescheduleAppointmentCommand command);
}
