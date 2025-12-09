package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.appointment.CancelAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.RescheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentCommand;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentResult;
import com.project.petshop_scheduler_chatbot.application.appointment.ScheduleAppointmentUseCase;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.AppointmentStatus;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AppointmentController.class)
public class ScheduleAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleAppointmentUseCase scheduleAppointmentUseCase;

    @MockitoBean
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    @MockitoBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;
    

    @Test
    public void testSchedule_Success() throws Exception {
        Long appointmentId = 1L;
        Long serviceId = 2L;
        Long professionalId = 3L;

        OffsetDateTime startAt = OffsetDateTime.parse("2100-12-09T10:00:00Z");
        OffsetDateTime endAt   = OffsetDateTime.parse("2100-12-09T11:00:00Z");

        ScheduleAppointmentResult result = new ScheduleAppointmentResult(appointmentId, serviceId, professionalId, "banho", startAt, endAt, AppointmentStatus.SCHEDULED);

        when(scheduleAppointmentUseCase.execute(Mockito.any(ScheduleAppointmentCommand.class))).thenReturn(result);

        String requestJson = """
        {
          "petId": 1,
          "tutorId": 2,
          "professionalId": 3,
          "serviceId": 2,
          "startAt": "2100-12-09T10:00:00Z",
          "observation": "Flor é idoso, cuidado com o ouvido"
        }
        """;       

        mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.appointmentId").value(1))
            .andExpect(jsonPath("$.serviceId").value(2))
            .andExpect(jsonPath("$.serviceName").value("banho"))
            .andExpect(jsonPath("$.professionalId").value(3))
            .andExpect(jsonPath("$.startAt").value("2100-12-09T10:00:00Z"))
            .andExpect(jsonPath("$.endAt").value("2100-12-09T11:00:00Z"))
            .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(scheduleAppointmentUseCase, times(1)).execute(Mockito.any(ScheduleAppointmentCommand.class));
    }

    @Test
    public void testSchedule_Error_TutorNotFoundExceptionShouldReturn404() throws Exception {
        when(scheduleAppointmentUseCase.execute(any(ScheduleAppointmentCommand.class)))
            .thenThrow(new TutorNotFoundException("Tutor inválido"));

        String requestJson = """
        {
        "petId": 1,
        "tutorId": 999,
        "professionalId": 5,
        "serviceId": 10,
        "startAt": "2100-12-09T10:00:00Z",
        "observation": "Flor é idoso, cuidado com o ouvido"
        }
        """; 

        mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TUTOR_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Tutor inválido"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/appointments"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(scheduleAppointmentUseCase, times(1)).execute(any(ScheduleAppointmentCommand.class));
    }

    @Test
    public void testSchedule_Error_ProfessionalNotFoundExceptionShouldReturn404() throws Exception {
        when(scheduleAppointmentUseCase.execute(any(ScheduleAppointmentCommand.class)))
            .thenThrow(new ProfessionalNotFoundException("Profissional inválido"));

        String requestJson = """
        {
        "petId": 1,
        "tutorId": 2,
        "professionalId": 999,
        "serviceId": 10,
        "startAt": "2100-12-09T10:00:00Z",
        "observation": "Flor é idoso, cuidado com o ouvido"
        }
        """; 

        mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Profissional inválido"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/appointments"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(scheduleAppointmentUseCase, times(1)).execute(any(ScheduleAppointmentCommand.class));
    }

    @Test
    public void testSchedule_Error_PetDoesNotBelongToTutorShouldReturn422() throws Exception {
               
        doThrow(new DomainValidationException("Pet não pertence ao tutor"))
        .when(scheduleAppointmentUseCase).execute(any(ScheduleAppointmentCommand.class));

        String requestJson = """
        {
        "petId": 1,
        "tutorId": 2,
        "professionalId": 999,
        "serviceId": 10,
        "startAt": "2100-12-09T10:00:00Z",
        "observation": "Flor é idoso, cuidado com o ouvido"
        }
        """; 

        mockMvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Pet não pertence ao tutor"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/appointments"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(scheduleAppointmentUseCase, times(1)).execute(any(ScheduleAppointmentCommand.class));
    }

    



}
