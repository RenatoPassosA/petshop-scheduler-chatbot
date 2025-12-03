package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PetController.class)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ScheduleAppointmentUseCase scheduleAppointmentUseCase;

    @Mock
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    @Mock
    private CancelAppointmentUseCase cancelAppointmentUseCase;


    @Test
    public void testSchedule() throws Exception {
        Long appointmentId = 1L;
        Long serviceId = 2L;
        Long professionalId = 3L;

        ScheduleAppointmentResult result = new ScheduleAppointmentResult(appointmentId, serviceId, professionalId, "banho", OffsetDateTime.now(), OffsetDateTime.now(), AppointmentStatus.SCHEDULED);

        when(scheduleAppointmentUseCase.execute(Mockito.any(ScheduleAppointmentCommand.class))).thenReturn(result);

        String requestJson = """
        {
          "petId": 1,
          "tutorId": 2,
          "professionalId": 5,
          "serviceId": 10,
          "startAt": "2025-01-20T10:00:00Z",
          "observation": "Flor é idoso, cuidado com o ouvido"
        }
        """;       

        mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.appointmentId").value(1))
            .andExpect(jsonPath("$.serviceId").value(10))
            .andExpect(jsonPath("$.serviceName").value("banho"))
            .andExpect(jsonPath("$.professionalId").value(5))
            .andExpect(jsonPath("$.startAt").value("2025-01-20T10:00:00Z"))
            .andExpect(jsonPath("$.endAt").value("2025-01-20T11:00:00Z"))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(scheduleAppointmentUseCase, times(1)).execute(Mockito.any(ScheduleAppointmentCommand.class));
    }

    @Test
    public void testeRescheduleAppointment() throws Exception {
        Long appointmentId = 1L;
        Long serviceId = 10L;
        Long professionalId = 5L;

        OffsetDateTime newStart = OffsetDateTime.parse("2025-01-21T15:00:00Z");
        OffsetDateTime newEnd   = OffsetDateTime.parse("2025-01-21T16:00:00Z");

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
            "newStartAt": "2025-01-21T15:00:00Z",
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
                .andExpect(jsonPath("$.startAt").value("2025-01-21T15:00:00Z"))
                .andExpect(jsonPath("$.endAt").value("2025-01-21T16:00:00Z"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(rescheduleAppointmentUseCase, times(1)).execute(any(RescheduleAppointmentCommand.class));
    }


    @Test
    public void testeCancelAppointment_Success() throws Exception {
        Long appointmentId = 1L;

        CancelAppointmentResult result = new CancelAppointmentResult(
            appointmentId,
            "banho",
            AppointmentStatus.CANCELED
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



}
