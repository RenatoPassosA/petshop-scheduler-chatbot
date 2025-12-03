package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddProfessionalResult;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffCommand;
import com.project.petshop_scheduler_chatbot.application.professional.AddTimeOffResult;
import com.project.petshop_scheduler_chatbot.application.professional.ProfessionalUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.TimeOffUseCase;
import com.project.petshop_scheduler_chatbot.application.professional.UpdateProfessionalCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Professional;
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
   

    
}


