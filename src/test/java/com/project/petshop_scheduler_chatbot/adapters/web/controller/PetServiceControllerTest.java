package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.hamcrest.Matchers.hasSize;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.management.ServiceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceCommand;
import com.project.petshop_scheduler_chatbot.application.petservices.AddPetServiceResult;
import com.project.petshop_scheduler_chatbot.application.petservices.PetServiceUseCase;
import com.project.petshop_scheduler_chatbot.application.petservices.UpdatePetServiceCommand;
import com.project.petshop_scheduler_chatbot.core.domain.PetService;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.InvalidAppointmentStateException;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PetServiceController.class)
public class PetServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PetServiceUseCase petServiceUseCase;

    @Test
    public void testAddPetService() throws Exception {
        
        AddPetServiceResult result = new AddPetServiceResult(1L, "tosa", new BigDecimal(100), 180);

        when(petServiceUseCase.register(Mockito.any(AddPetServiceCommand.class))).thenReturn(result);

        String requestJson = """
            {
            "name": "tosa",
            "price": "100",
            "duration": "180"
            }
            """;

        mockMvc.perform(post("/petservice")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("tosa"))
        .andExpect(jsonPath("$.price").value("100"))
        .andExpect(jsonPath("$.duration").value("180"));

        verify(petServiceUseCase, times(1)).register(Mockito.any(AddPetServiceCommand.class));
    }

    @Test
    public void testUpdatePetService() throws Exception {
        Long serviceId  = 1L;

        doNothing().when(petServiceUseCase).update(eq(serviceId ), any(UpdatePetServiceCommand.class));

        mockMvc.perform(put("/petservice/{id}", serviceId )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"tosa premium\", \"price\": \"120\", \"duration\": \"220\"   }"))
                .andExpect(status().isNoContent()
            );

        verify(petServiceUseCase, times(1)).update(eq(serviceId ), any(UpdatePetServiceCommand.class));
    }

    @Test
    public void testGetPetService() throws Exception {
        Long serviceId = 1L;
        PetService petService = new PetService("massagem", new BigDecimal(220), 60, OffsetDateTime.now(), OffsetDateTime.now());

        when(petServiceUseCase.getPetService(serviceId)).thenReturn(petService);

        mockMvc.perform(get("/petservice/{id}", serviceId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("massagem"))
        .andExpect(jsonPath("$.price").value(220))
        .andExpect(jsonPath("$.duration").value(60));

        verify(petServiceUseCase, times(1)).getPetService(serviceId);
    }

    @Test
    public void getAll() throws Exception {
        List<PetService> services = new ArrayList<>();
        services.add(new PetService("massagem", new BigDecimal(220), 60, OffsetDateTime.now(), OffsetDateTime.now()));
        services.add(new PetService("tosa", new BigDecimal(70), 90, OffsetDateTime.now(), OffsetDateTime.now()));
        services.add(new PetService("banho", new BigDecimal(50), 60, OffsetDateTime.now(), OffsetDateTime.now()));
        services.add(new PetService("banho e tosa", new BigDecimal(110), 150, OffsetDateTime.now(), OffsetDateTime.now()));
    
        when(petServiceUseCase.getAll()).thenReturn(services);

        mockMvc.perform(get("/petservice/all").
        contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(4)))

        .andExpect(jsonPath("$[0].name").value("massagem"))
        .andExpect(jsonPath("$[0].price").value(220))
        .andExpect(jsonPath("$[0].duration").value(60))

        .andExpect(jsonPath("$[1].name").value("tosa"))
        .andExpect(jsonPath("$[1].price").value(70))
        .andExpect(jsonPath("$[1].duration").value(90))

        .andExpect(jsonPath("$[2].name").value("banho"))
        .andExpect(jsonPath("$[2].price").value(50))
        .andExpect(jsonPath("$[2].duration").value(60))

        .andExpect(jsonPath("$[3].name").value("banho e tosa"))
        .andExpect(jsonPath("$[3].price").value(110))
        .andExpect(jsonPath("$[3].duration").value(150));

        verify(petServiceUseCase, times(1)).getAll();
    }

    @Test
    public void deletePetService() throws Exception {
        Long serviceId = 1L;
        
        doNothing().when(petServiceUseCase).delete(serviceId);

        mockMvc.perform(delete("/petservice/{id}", serviceId))
            .andExpect(status().isNoContent()); 

        verify(petServiceUseCase, times(1)).delete(serviceId);
    }




    @Test
    public void testAddPetService_ErrorInvalidAppointmentStateException_ShouldReturn409() throws Exception {
        when(petServiceUseCase.register(any(AddPetServiceCommand.class)))
            .thenThrow(new InvalidAppointmentStateException("Serviço já cadastrado"));

         String requestJson = """
                            {
                            "name": "tosa premium",
                            "price": "200",
                            "duration": "150"
                            }
                            """;

        mockMvc.perform(post("/petservice")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("INVALID_APPOINTMENT_STATE"))
            .andExpect(jsonPath("$.message").value("Serviço já cadastrado"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.path").value("/petservice"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petServiceUseCase, times(1)).register(any(AddPetServiceCommand.class));
    }

    @Test
    public void testAddPetService_ErrorDomainValidation_ShouldReturn422() throws Exception {
        when(petServiceUseCase.register(any(AddPetServiceCommand.class)))
            .thenThrow(new DomainValidationException("Nome do Serviço é obrigatório"));

         String requestJson = """
                            {
                            "name": "",
                            "price": "200",
                            "duration": "150"
                            }
                            """;

        mockMvc.perform(post("/petservice")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Nome do Serviço é obrigatório"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/petservice"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petServiceUseCase, times(1)).register(any(AddPetServiceCommand.class));
    }

    @Test
    public void testUpdatePetService_NotFound_ShouldReturn404() throws Exception {
        Long serviceId = 99L;

        doThrow(new ServiceNotFoundException("Service id: " + serviceId + " não encontrado"))
        .when(petServiceUseCase)
        .update(eq(serviceId), any(UpdatePetServiceCommand.class));

        String requestJson = """
                            {
                            "name": "tosa premium",
                            "price": "200",
                            "duration": "150"
                            }
                            """;

        mockMvc.perform(put("/petservice/{id}", serviceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("SERVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Service id: " + serviceId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/petservice/" + serviceId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petServiceUseCase, times(1)).update(eq(serviceId), any(UpdatePetServiceCommand.class));
    }

    @Test
    public void testGetPetService_NotFound_ShouldReturn404() throws Exception {
        Long serviceId = 99L;

        when(petServiceUseCase.getPetService(serviceId)).thenThrow(new ServiceNotFoundException("Service id: " + serviceId + " não encontrado"));

        mockMvc.perform(get("/petservice/{id}", serviceId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("SERVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Service id: " + serviceId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/petservice/" + serviceId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petServiceUseCase, times(1)).getPetService(serviceId);
    }

    
    @Test
    public void testDeletePetService_NotFound_ShouldReturn404() throws Exception {
        Long serviceId = 99L;

        doThrow(new ServiceNotFoundException("Service id: " + serviceId + " não encontrado"))
        .when(petServiceUseCase)
        .delete(serviceId);

        mockMvc.perform(delete("/petservice/{id}", serviceId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("SERVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Service id: " + serviceId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/petservice/" + serviceId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petServiceUseCase, times(1)).delete(serviceId);
    }
}