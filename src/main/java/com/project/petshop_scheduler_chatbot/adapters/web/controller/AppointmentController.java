package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.petshop_scheduler_chatbot.adapters.web.dto.CancelAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.CancelAppointmentResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.RescheduleAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.RescheduleAppointmentResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.ScheduleAppointmentRequest;
import com.project.petshop_scheduler_chatbot.adapters.web.dto.ScheduleAppointmentResponse;
import com.project.petshop_scheduler_chatbot.adapters.web.mapper.AppointmentWebMapper;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final ScheduleAppointmentUseCase scheduleAppointmentUseCase;
    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    public AppointmentController (ScheduleAppointmentUseCase scheduleAppointmentUseCase,
                                RescheduleAppointmentUseCase rescheduleAppointmentUseCase,
                                CancelAppointmentUseCase cancelAppointmentUseCase) {
        this.scheduleAppointmentUseCase = scheduleAppointmentUseCase;
        this.rescheduleAppointmentUseCase = rescheduleAppointmentUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<ScheduleAppointmentResponse> schedule (@RequestBody @Valid ScheduleAppointmentRequest request) {
        ScheduleAppointmentCommand command = AppointmentWebMapper.toCommand(request);
        ScheduleAppointmentResult result = scheduleAppointmentUseCase.execute(command);
        ScheduleAppointmentResponse response = AppointmentWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<RescheduleAppointmentResponse> reschedule(@PathVariable("id") @Positive Long id, @RequestBody @Valid RescheduleAppointmentRequest request) {
        RescheduleAppointmentCommand command = AppointmentWebMapper.toCommand(id, request);
        RescheduleAppointmentResult result = rescheduleAppointmentUseCase.execute(command);
        RescheduleAppointmentResponse response = AppointmentWebMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CancelAppointmentResponse> cancelAppointment (@PathVariable("id") @Positive Long id, @RequestBody(required = false) @Valid CancelAppointmentRequest request) {
        CancelAppointmentCommand command = AppointmentWebMapper.toCommand(id, request);
        CancelAppointmentResult result = cancelAppointmentUseCase.execute(command);
        CancelAppointmentResponse response = AppointmentWebMapper.toResponse(result);
        return ResponseEntity.ok(response);
    }

}
