package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.AppointmentNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AppointmentController.class)
public class CancelAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleAppointmentUseCase scheduleAppointmentUseCase;

    @MockitoBean
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    @MockitoBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @Test
    public void testCancelAppointment_Success() throws Exception {
        Long appointmentId = 1L;

        CancelAppointmentResult result = new CancelAppointmentResult(
            appointmentId,
            "banho",
            AppointmentStatus.CANCELLED
        );

        when(cancelAppointmentUseCase.execute(any(CancelAppointmentCommand.class)))
            .thenReturn(result);

        String requestJson = """
            {
            "reason": "Cliente desistiu"
            }
            """;

        mockMvc.perform(delete("/appointments/{id}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.serviceName").value("banho"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(cancelAppointmentUseCase, times(1)).execute(any(CancelAppointmentCommand.class));
    }

    @Test
    public void testCancelAppointment_Error_AppointmentNotFoundExceptionShouldReturn404() throws Exception {
        Long appointmentId = 1L;

        when(cancelAppointmentUseCase.execute(any(CancelAppointmentCommand.class)))
            .thenThrow(new AppointmentNotFoundException("Agendamento não encontrado"));

            

        String requestJson = """
            {
            "newStartAt": "2100-12-09T10:00:00Z",
            "observation": "desistiu"
            }
            """;

        mockMvc.perform(delete("/appointments/{id}", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("APPOINTMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Agendamento não encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/appointments/" + appointmentId))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(cancelAppointmentUseCase, times(1)).execute(any(CancelAppointmentCommand.class));
    }



}
