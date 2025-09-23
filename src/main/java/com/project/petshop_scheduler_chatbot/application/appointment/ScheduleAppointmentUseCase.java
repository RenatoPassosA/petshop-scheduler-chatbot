package com.project.petshop_scheduler_chatbot.application.appointment;

public interface ScheduleAppointmentUseCase {
    ScheduleAppointmentResult execute (ScheduleAppointmentCommand command);
}
