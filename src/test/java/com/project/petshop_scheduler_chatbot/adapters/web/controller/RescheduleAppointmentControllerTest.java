package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AppointmentController.class)
public class RescheduleAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleAppointmentUseCase scheduleAppointmentUseCase;

    @MockitoBean
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    @MockitoBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @Test
    public void testRescheduleAppointment_Success() throws Exception {
        Long appointmentId = 1L;
        Long serviceId = 10L;
        Long professionalId = 5L;

        OffsetDateTime newStart = OffsetDateTime.parse("2100-12-09T10:00:00Z");
        OffsetDateTime newEnd   = OffsetDateTime.parse("2100-12-09T12:00:00Z");

        RescheduleAppointmentResult result = new RescheduleAppointmentResult(
            appointmentId,
            serviceId,
            professionalId,
            newStart,
            newEnd,
            AppointmentStatus.SCHEDULED
        );

        when(rescheduleAppointmentUseCase.execute(any(RescheduleAppointmentCommand.class)))
            .thenReturn(result);

        String requestJson = """
            {
            "newStartAt": "2100-12-09T10:00:00Z",
            "observation": "Remarcado a pedido do cliente"
            }
            """;

        mockMvc.perform(put("/appointments/{id}/reschedule", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.serviceId").value(10))
                .andExpect(jsonPath("$.professionalId").value(5))
                .andExpect(jsonPath("$.startAt").value("2100-12-09T10:00:00Z"))
                .andExpect(jsonPath("$.endAt").value("2100-12-09T12:00:00Z"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(rescheduleAppointmentUseCase, times(1)).execute(any(RescheduleAppointmentCommand.class));
    }

    @Test
    public void testReschedule_Error_TutorNotFoundExceptionShouldReturn404() throws Exception {
        Long appointmentId = 1L;

        when(rescheduleAppointmentUseCase.execute(any(RescheduleAppointmentCommand.class)))
            .thenThrow(new WorkingHoursOutsideException("Horário fora do expediente"));

            

        String requestJson = """
            {
            "newStartAt": "2100-12-09T10:00:00Z",
            "observation": "Remarcado a pedido do cliente"
            }
            """;

        mockMvc.perform(put("/appointments/{id}/reschedule", appointmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKING_HOURS_OUTSIDE"))
                .andExpect(jsonPath("$.message").value("Horário fora do expediente"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/appointments/" + appointmentId + "/reschedule"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(rescheduleAppointmentUseCase, times(1)).execute(any(RescheduleAppointmentCommand.class));
    }
}
