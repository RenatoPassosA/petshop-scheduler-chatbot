package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalNotFoundException;
import com.project.petshop_scheduler_chatbot.application.exceptions.ProfessionalTimeOffException;
import com.project.petshop_scheduler_chatbot.application.exceptions.WorkingHoursOutsideException;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalResult;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.application.professional.ProfessionalUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.TimeOffUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Office;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProfessionalController.class)
public class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProfessionalUseCase professionalUseCase;
    @Mock
    private TimeOffUseCase timeOffUseCase;

    @Test
    public void testAddProfessional() throws Exception {
        AddProfessionalResult result = new AddProfessionalResult(1L, "renato", Office.TOSADOR);
        when(professionalUseCase.execute(Mockito.any(AddProfessionalCommand.class))).thenReturn(result);

        mockMvc.perform(post("/professional")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"renato\", \"function\": \"TOSADOR\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("renato"))
                .andExpect(jsonPath("$.function").value("TOSADOR"));

        
        verify(professionalUseCase, times(1)).execute(Mockito.any(AddProfessionalCommand.class));
    }

    @Test
    public void testAddTimeOff() throws Exception {
        Long professionalId = 1L;
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-20T10:00:00Z");
        OffsetDateTime endAt   = OffsetDateTime.parse("2025-01-23T10:00:00Z");

        AddTimeOffResult result = new AddTimeOffResult(professionalId, "renato", "consulta médica", startAt, endAt);


        when(timeOffUseCase.execute(Mockito.any(AddTimeOffCommand.class))).thenReturn(result);

        mockMvc.perform(post("/professional/{id}/timeoff", professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"reason\": \"consulta médica\", \"startAt\": \"2025-01-20T10:00:00Z\", \"endAt\": \"2025-01-23T10:00:00Z\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.professionalId").value(1))
                .andExpect(jsonPath("$.professionalName").value("renato"))
                .andExpect(jsonPath("$.reason").value("consulta médica"))
                .andExpect(jsonPath("$.startAt").value("2025-01-20T10:00:00Z"))
                .andExpect(jsonPath("$.endAt").value("2025-01-23T10:00:00Z"));

        verify(timeOffUseCase, times(1)).execute(Mockito.any(AddTimeOffCommand.class));
    }

    @Test
    public void testGetProfessional() throws Exception {
        Long professionalId = 1L;
        Professional professional = new Professional("renato", Office.AUX, OffsetDateTime.now(), OffsetDateTime.now());
        
        when(professionalUseCase.getProfessional(professionalId)).thenReturn(professional);
        

        mockMvc.perform(get("/professional/{id}", professionalId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renato"))
                .andExpect(jsonPath("$.function").value("AUX"));

        verify(professionalUseCase, times(1)).getProfessional(professionalId);
    }

    @Test
    public void testUpdateProfessional() throws Exception {
        Long professionalId = 1L;

        doNothing().when(professionalUseCase).update(eq(professionalId), any(UpdateProfessionalCommand.class));

        mockMvc.perform(put("/professional/{id}", professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"amanda\", \"function\": \"VET\" }"))
                .andExpect(status().isNoContent()
            );

        verify(professionalUseCase, times(1)).update(eq(professionalId), any(UpdateProfessionalCommand.class));
    }

    @Test
    public void deleteProfessional() throws Exception {
        Long professionalId = 1L;
        
        doNothing().when(professionalUseCase).delete(professionalId);

        mockMvc.perform(delete("/professional/{id}", professionalId))
            .andExpect(status().isNoContent()); 

        verify(professionalUseCase, times(1)).delete(professionalId);
    }

    @Test
    public void deleteTimeOff() throws Exception {
        Long professionalId = 1L;
        Long timeOffId = 2L;
        
        doNothing().when(timeOffUseCase).delete(professionalId, timeOffId);

        mockMvc.perform(delete("/professional/{professionalId}/timeoff/{timeOffId}", professionalId, timeOffId))
            .andExpect(status().isNoContent()); 

        verify(timeOffUseCase, times(1)).delete(professionalId, timeOffId);
    }

    @Test
    public void testAddProfessional_ErrorDomainValidation_ShouldReturn422() throws Exception {
        when(professionalUseCase.execute(any(AddProfessionalCommand.class)))
            .thenThrow(new DomainValidationException("Nome do Colaborador é obrigatório"));

         String requestJson = """
                            {
                            "name": "",
                            "function": "Office.VET"
                            }
                            """;

        mockMvc.perform(post("/professional")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Nome do Colaborador é obrigatório"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/professional"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(professionalUseCase, times(1)).execute(any(AddProfessionalCommand.class));
    }
   
    @Test
    public void testUpdateProfessional_NotFound_ShouldReturn404() throws Exception {
        Long professionalId = 99L;

        doThrow(new ProfessionalNotFoundException("Professional id: " + professionalId + " não encontrado"))
        .when(professionalUseCase)
        .update(eq(professionalId), any(UpdateProfessionalCommand.class));

        String requestJson = """
                            {
                            "name": "amanda",
                            "function": "Office.VET"
                            }
                            """;

        mockMvc.perform(put("/professional/{id}", professionalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Professional id: " + professionalId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/professional/" + professionalId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(professionalUseCase, times(1)).update(eq(professionalId), any(UpdateProfessionalCommand.class));
    }

    @Test
    public void testGetProfessional_NotFound_ShouldReturn404() throws Exception {
        Long professionalId = 99L;

        when(professionalUseCase.getProfessional(professionalId)).thenThrow(new ProfessionalNotFoundException("Professional id: " + professionalId + " não encontrado"));

        mockMvc.perform(get("/professional/{id}", professionalId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Professional id: " + professionalId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/professional/" + professionalId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(professionalUseCase, times(1)).getProfessional(professionalId);
    }

    
    @Test
    public void testDeleteProfessional_NotFound_ShouldReturn404() throws Exception {
        Long professionalId = 99L;

        doThrow(new ProfessionalNotFoundException("Professional id: " + professionalId + " não encontrado"))
        .when(professionalUseCase)
        .delete(professionalId);

        mockMvc.perform(delete("/professional/{id}", professionalId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Professional id: " + professionalId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/professional/" + professionalId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(professionalUseCase, times(1)).delete(professionalId);
    }

    @Test
    public void testAddTimeOff_ErrorDomainValidation_ShouldReturn422() throws Exception {
        Long professionalId = 1L;
        when(timeOffUseCase.execute(any(AddTimeOffCommand.class)))
            .thenThrow(new DomainValidationException("Id do profissional inválido"));

         String requestJson = """
                            {
                            "reason": "consulta médica",
                            "startAt": "2025-01-20T10:00:00Z",
                            "endAt": "2025-01-21T10:00:00Z"
                            }
                            """;

        mockMvc.perform(post("/professional/{id}/timeoff", professionalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Id do profissional inválido"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/professional/1/timeoff"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(timeOffUseCase, times(1)).execute(any(AddTimeOffCommand.class));
    }

    @Test
    public void testAddTimeOff_ProfessionalNotFoundException_ShouldReturn404() throws Exception {
        Long professionalId = 1L;
        when(timeOffUseCase.execute(any(AddTimeOffCommand.class)))
            .thenThrow(new ProfessionalNotFoundException("Profissional nao cadastrado"));

         String requestJson = """
                            {
                            "reason": "consulta médica",
                            "startAt": "2025-01-20T10:00:00Z",
                            "endAt": "2025-01-21T10:00:00Z"
                            }
                            """;

        mockMvc.perform(post("/professional/{id}/timeoff", professionalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Profissional nao cadastrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/professional/1/timeoff"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(timeOffUseCase, times(1)).execute(any(AddTimeOffCommand.class));
    }

    @Test
    public void testAddTimeOff_WorkingHoursOutsideException_ShouldReturn409() throws Exception {
        Long professionalId = 1L;
        when(timeOffUseCase.execute(any(AddTimeOffCommand.class)))
            .thenThrow(new WorkingHoursOutsideException("Horário fora do expediente"));

         String requestJson = """
                            {
                            "reason": "consulta médica",
                            "startAt": "2025-01-20T10:00:00Z",
                            "endAt": "2025-01-21T10:00:00Z"
                            }
                            """;

        mockMvc.perform(post("/professional/{id}/timeoff", professionalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("WORKING_HOURS_OUTSIDE"))
            .andExpect(jsonPath("$.message").value("Horário fora do expediente"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.path").value("/professional/1/timeoff"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(timeOffUseCase, times(1)).execute(any(AddTimeOffCommand.class));
    }

    @Test
    public void testAddTimeOff_ProfessionalTimeOffException_ShouldReturn409() throws Exception {
        Long professionalId = 1L;
        when(timeOffUseCase.execute(any(AddTimeOffCommand.class)))
            .thenThrow(new ProfessionalTimeOffException("Folga já cadastrada"));

         String requestJson = """
                            {
                            "reason": "consulta médica",
                            "startAt": "2025-01-20T10:00:00Z",
                            "endAt": "2025-01-21T10:00:00Z"
                            }
                            """;

        mockMvc.perform(post("/professional/{id}/timeoff", professionalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_TIME_OFF"))
            .andExpect(jsonPath("$.message").value("Folga já cadastrada"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.path").value("/professional/1/timeoff"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(timeOffUseCase, times(1)).execute(any(AddTimeOffCommand.class));
    }
    
    @Test
    public void testDeleteProfessionalTimeOffException_ShouldReturn409() throws Exception {
        Long timeOffId = 99L;
        Long professionalId = 1L;

        doThrow(new ProfessionalTimeOffException("TimeOff não encontrado"))
        .when(timeOffUseCase)
        .delete(professionalId, timeOffId);

        mockMvc.perform(delete("/professional/{id}/timeoff/{timeOffId}", professionalId, timeOffId))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PROFESSIONAL_TIME_OFF"))
            .andExpect(jsonPath("$.message").value("TimeOff não encontrado"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.path").value("/professional/1/timeoff/99"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(timeOffUseCase, times(1)).delete(professionalId, timeOffId);
    }
    
}


