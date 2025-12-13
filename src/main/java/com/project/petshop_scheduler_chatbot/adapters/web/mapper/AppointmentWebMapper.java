package com.project.petshop_scheduler_chatbot.adapters.web.mapper;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.CancelAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.CancelAppointmentResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.RescheduleAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.RescheduleAppointmentResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.ScheduleAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.appointment.ScheduleAppointmentResponse;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;

public class AppointmentWebMapper {
    static public ScheduleAppointmentResponse toResponse(ScheduleAppointmentResult r) {
        return new ScheduleAppointmentResponse(r.getAppointmentId(),
                                            r.getServiceId(),
                                            r.getServiceName(),
                                            r.getProfessionalId(),
                                            r.getStartAt(),
                                            r.getEndAt(),
                                            r.getStatus().name()); 
    }

    static public RescheduleAppointmentResponse toResponse(RescheduleAppointmentResult r) {
        return new RescheduleAppointmentResponse(r.getAppointmentId(),
                                            r.getServiceId(),
                                            r.getProfessionalId(),
                                            r.getStartAt(),
                                            r.getEndAt(),
                                            r.getStatus().name()); 
    }

    static public CancelAppointmentResponse toResponse(CancelAppointmentResult r) {
        return new CancelAppointmentResponse(r.getAppointmentId(),
                                            r.getServiceName(),
                                            r.getStatus().name()); 
    }

    static public ScheduleAppointmentCommand toCommand(ScheduleAppointmentRequest r) {
        return new ScheduleAppointmentCommand(r.getPetId(),
                                            r.getTutorId(),
                                            r.getProfessionalId(),
                                            r.getServiceId(),
                                            r.getStartAt(),
                                            r.getObservation()
                                            );
    }

    static public RescheduleAppointmentCommand toCommand(Long appointmentId, RescheduleAppointmentRequest r){
        return new RescheduleAppointmentCommand(appointmentId, r.getNewStartAt());
    }

    static public CancelAppointmentCommand toCommand(Long appointmentId, CancelAppointmentRequest r){
        return new CancelAppointmentCommand(appointmentId);
    }
}
