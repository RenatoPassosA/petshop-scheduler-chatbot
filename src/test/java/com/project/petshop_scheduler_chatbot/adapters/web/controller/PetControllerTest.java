package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.exceptions.PetNotFoundException;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorResult;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.application.pet.UpdatePetCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.Pet;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(PetController.class)
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetUseCase petUseCase;

    @MockitoBean
    private TutorUseCase tutorUseCase;

    @Test
    public void testAddPetToTutor() throws Exception {
        Long tutorId = 1L;
        Long petId = 2L;
        Tutor tutor = new Tutor("renato", new PhoneNumber("123456789"), "rua 1", OffsetDateTime.now(), OffsetDateTime.now());
        AddPetToTutorResult result = new AddPetToTutorResult(petId, tutorId,"flor", "ok");

        when(tutorUseCase.getTutor(tutorId)).thenReturn(tutor);
        when(petUseCase.execute(Mockito.any(AddPetToTutorCommand.class))).thenReturn(result);

        String requestJson = """
            {
            "name": "flor",
            "gender": "F",
            "size": "SMALL",
            "breed": "york",
            "tutorId": 1,
            "observation": "ok"
            }
            """;

        mockMvc.perform(post("/pet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petId").value(2))
            .andExpect(jsonPath("$.petName").value("flor"))
            .andExpect(jsonPath("$.tutorId").value(1))
            .andExpect(jsonPath("$.tutorName").value("renato"));

        verify(tutorUseCase, times(1)).getTutor(tutorId);
        verify(petUseCase, times(1)).execute(Mockito.any(AddPetToTutorCommand.class));

        /*
        primeiro mock:
        aqui eu estou passando o objeto tutor que eu criei no teste para dentro do controller via mock. O objeto Tutor é injetado no fluxo do controller através do mock.
        Lá dentro ele faz o get name e pega esse nome.
        o nome junto com mais outras infos, que é o meu response, é passado como resposta http pro teste.
        no teste é feita a conferencia se o nome e das outras infos retornadas usando o jsonPath
        
        segundo mock:
        eu passo o result criado pra dentro do controller o controller vai mapear o result em response, e o conteudo do response vai ser passado pro teste via
        http para eu conferir os resultados
        
        
        .content(requestJson)):
        isso é o conteúdo do parametro request dos métodos que o pedem
        Os metodos get() nao tem .content() porque eles nao enviam nada de requisição, somente o id que consta da rota

        */
    }

    @Test
    public void testUpdatePet_Success() throws Exception {
        Long petId = 1L;

        doNothing().when(petUseCase).update(eq(petId), any(UpdatePetCommand.class));

        mockMvc.perform(put("/pet/{id}", petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"observations\": \"alergia a tal shampoo\" }"))
                .andExpect(status().isNoContent()
            );

        verify(petUseCase, times(1)).update(eq(petId), any(UpdatePetCommand.class));
    }

    @Test
    public void testGetPet_Success() throws Exception {
        Long petId = 1L;
        Tutor tutor = new Tutor("renato", new PhoneNumber("123456789"), "rua 1", OffsetDateTime.now(), OffsetDateTime.now());
        Pet pet = new Pet("flor", Gender.F, PetSize.SMALL, "york", 1L, "ok", OffsetDateTime.now(), OffsetDateTime.now());

        when(petUseCase.getPet(petId)).thenReturn(pet);
        when(tutorUseCase.getTutor(pet.getTutorId())).thenReturn(tutor);

        mockMvc.perform(get("/pet/{id}", petId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("flor"))
                .andExpect(jsonPath("$.tutorName").value("renato"))
                .andExpect(jsonPath("$.gender").value("F"))
                .andExpect(jsonPath("$.size").value("SMALL"))
                .andExpect(jsonPath("$.breed").value("york"))
                .andExpect(jsonPath("$.tutorId").value(1))
                .andExpect(jsonPath("$.observations").value("ok")
            );

        verify(tutorUseCase, times(2)).getTutor(pet.getTutorId());
        verify(petUseCase, times(1)).getPet(petId);
    }


    @Test
    public void getAll_Success() throws Exception {
        Long tutorId = 1L;
        OffsetDateTime date = OffsetDateTime.now();
        Tutor tutor = new Tutor("renato", new PhoneNumber("123345678"), "rua 3", OffsetDateTime.now(), OffsetDateTime.now());
        List<Pet> pets = new ArrayList<>();
        pets.add(new Pet("flor", Gender.F, PetSize.SMALL, "york", tutorId, "ok", date, date));
        pets.add(new Pet("kiwi", Gender.M, PetSize.MEDIUM, "shitzu", tutorId, "ok", date, date));
        pets.add(new Pet("manu", Gender.F, PetSize.LARGE, "dalmata", tutorId, "ok", date, date));
        pets.add(new Pet("luke", Gender.M, PetSize.SMALL, "york", tutorId, "ok", date, date));
        pets.add(new Pet("zeus", Gender.M, PetSize.LARGE, "dalmata", tutorId, "ok", date, date));
    
        when(petUseCase.getAll()).thenReturn(pets);
        when(tutorUseCase.getTutor(tutorId)).thenReturn(tutor);

        mockMvc.perform(get("/pet/all").
        contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(5))) 

        .andExpect(jsonPath("$[0].name").value("flor")) 
        .andExpect(jsonPath("$[0].tutorName").value("renato"))
        .andExpect(jsonPath("$[0].name").value("flor"))
        .andExpect(jsonPath("$[0].size").value("SMALL"))
        .andExpect(jsonPath("$[0].breed").value("york"))
        .andExpect(jsonPath("$[0].tutorId").value(1))
        .andExpect(jsonPath("$[0].observations").value("ok"))

        .andExpect(jsonPath("$[1].name").value("kiwi"))
        .andExpect(jsonPath("$[1].tutorName").value("renato"))
        .andExpect(jsonPath("$[1].gender").value("M"))
        .andExpect(jsonPath("$[1].size").value("MEDIUM"))
        .andExpect(jsonPath("$[1].breed").value("shitzu"))
        .andExpect(jsonPath("$[1].tutorId").value(1))
        .andExpect(jsonPath("$[1].observations").value("ok"))

        .andExpect(jsonPath("$[2].name").value("manu"))
        .andExpect(jsonPath("$[2].tutorName").value("renato"))
        .andExpect(jsonPath("$[2].gender").value("F"))
        .andExpect(jsonPath("$[2].size").value("LARGE"))
        .andExpect(jsonPath("$[2].breed").value("dalmata"))
        .andExpect(jsonPath("$[2].tutorId").value(1))
        .andExpect(jsonPath("$[2].observations").value("ok"))

        .andExpect(jsonPath("$[3].name").value("luke"))
        .andExpect(jsonPath("$[3].tutorName").value("renato"))
        .andExpect(jsonPath("$[3].gender").value("M"))
        .andExpect(jsonPath("$[3].size").value("SMALL"))
        .andExpect(jsonPath("$[3].breed").value("york"))
        .andExpect(jsonPath("$[3].tutorId").value(1))
        .andExpect(jsonPath("$[3].observations").value("ok"))

        .andExpect(jsonPath("$[4].name").value("zeus"))
        .andExpect(jsonPath("$[4].tutorName").value("renato"))
        .andExpect(jsonPath("$[4].gender").value("M"))
        .andExpect(jsonPath("$[4].size").value("LARGE"))
        .andExpect(jsonPath("$[4].breed").value("dalmata"))
        .andExpect(jsonPath("$[4].tutorId").value(1))
        .andExpect(jsonPath("$[4].observations").value("ok"));

        verify(tutorUseCase, times(10)).getTutor(tutorId);
        verify(petUseCase, times(1)).getAll();
    }

    @Test
    public void deletePet_Success() throws Exception {
        Long petId = 1L;
        
        doNothing().when(petUseCase).delete(petId);

        mockMvc.perform(delete("/pet/{id}", petId))
            .andExpect(status().isNoContent()); 

        verify(petUseCase, times(1)).delete(petId);
    }

    @Test
    public void testAddPet_ErrorDomainValidation_ShouldReturn422() throws Exception {
        Tutor tutor = new Tutor("renato", new PhoneNumber("123345678"), "rua 3", OffsetDateTime.now(), OffsetDateTime.now());

        when(tutorUseCase.getTutor(anyLong())).thenReturn(tutor);
        when(petUseCase.execute(any(AddPetToTutorCommand.class)))
            .thenThrow(new DomainValidationException("Nome do Pet é obrigatório"));

         String requestJson = """
                            {
                            "name": "",
                            "gender": "F",
                            "size": "SMALL",
                            "breed": "york",
                            "tutorId": "1",
                            "observation": "idoso"
                            }
                            """;

        mockMvc.perform(post("/pet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Nome do Pet é obrigatório"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/pet"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).getTutor(anyLong());
        verify(petUseCase, times(1)).execute(any(AddPetToTutorCommand.class));
    }

    @Test
    public void testUpdatePet_NotFound_ShouldReturn404() throws Exception {
        Long petId = 99L;

        doThrow(new PetNotFoundException("Pet não encontrado"))
        .when(petUseCase)
        .update(eq(petId), any(UpdatePetCommand.class));

        String requestJson = """
                            {
                            "observations": "idoso"
                            }
                            """;

        mockMvc.perform(put("/pet/{id}", petId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PET_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Pet não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/pet/" + petId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petUseCase, times(1)).update(eq(petId), any(UpdatePetCommand.class));
    }

    @Test
    public void testGetPet_NotFound_ShouldReturn404() throws Exception {
        Long petId = 99L;

        when(petUseCase.getPet(petId)).thenThrow(new PetNotFoundException("Pet não encontrado"));

        mockMvc.perform(get("/pet/{id}", petId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PET_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Pet não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/pet/" + petId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petUseCase, times(1)).getPet(petId);
    }

    
    @Test
    public void testDeletePet_NotFound_ShouldReturn404() throws Exception {
        Long petId = 99L;

        doThrow(new PetNotFoundException("Pet não encontrado"))
        .when(petUseCase)
        .delete(petId);

        mockMvc.perform(delete("/pet/{id}", petId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PET_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Pet não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/pet/" + petId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(petUseCase, times(1)).delete(petId);
    }
}

/*
Quando tiver esse padrão:

controller recebe DTO (Request) ->
converte para Command com mapper ->
chama useCase.execute(command)

Então, no teste de controller:

no stub (when) → use any(SeuCommand.class) ->
no verify → use any(SeuCommand.class) (e eq() pros tipos simples se quiser checar o ID) ->
Porque o command criado no controller nunca vai ser a mesma instância que você criou no teste. 



Nos testes de PUT (quando o endpoint segue o padrão REST correto), o que deve ser validado é:
- O endpoint aceita o JSON enviado no body
- O controller chama o use case com: ID certo e qualquer command válido (any(classe.class))
- O endpoint retorna o status HTTP correto
*/