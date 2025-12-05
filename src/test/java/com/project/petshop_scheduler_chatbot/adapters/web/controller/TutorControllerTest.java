package com.project.petshop_scheduler_chatbot.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.petshop_scheduler_chatbot.application.exceptions.DuplicatedPhoneNumberException;
import com.project.petshop_scheduler_chatbot.application.exceptions.TutorNotFoundException;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.application.tutor.UpdateTutorCommand;
import com.project.petshop_scheduler_chatbot.core.domain.Tutor;
import com.project.petshop_scheduler_chatbot.core.domain.exceptions.DomainValidationException;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(TutorController.class)
public class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TutorUseCase tutorUseCase;

    @Test
    public void testAddTutor() throws Exception {
        
        AddTutorResult result = new AddTutorResult(1L, "Renato", "123456789", "rua 1");
        when(tutorUseCase.execute(any(AddTutorCommand.class))).thenReturn(result);

        mockMvc.perform(post("/tutor")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Renato\", \"phoneNumber\": \"123456789\", \"address\": \"rua 1\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Renato"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.address").value("rua 1"));

        verify(tutorUseCase, times(1)).execute(any(AddTutorCommand.class));
    }

    @Test
    public void testUpdateTutor() throws Exception {
        Long tutorId = 1L;

        doNothing().when(tutorUseCase).update(eq(tutorId), any(UpdateTutorCommand.class));

        mockMvc.perform(put("/tutor/{id}", tutorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Renato\", \"phoneNumber\": \"987654321\", \"address\": \"rua 2\" }"))
                .andExpect(status().isNoContent());

        verify(tutorUseCase, times(1)).update(eq(tutorId), any(UpdateTutorCommand.class));
    }

    @Test
    public void testGetTutor() throws Exception {
        Long tutorId = 1L;
        Tutor tutor = new Tutor("renato", new PhoneNumber("123345678"), "rua 3", OffsetDateTime.now(), OffsetDateTime.now());

        when(tutorUseCase.getTutor(tutorId)).thenReturn(tutor);


        mockMvc.perform(get("/tutor/{id}", tutorId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("renato"))
        .andExpect(jsonPath("$.phoneNumber").value("123345678"))
        .andExpect(jsonPath("$.address").value("rua 3"));

        verify(tutorUseCase, times(1)).getTutor(tutorId);

        /*
        eu estou chamando o meu controlador para executar o metodo get
        mockMvc.perform(get("/tutor/{id}", tutorId)) - aqui eu simulo o get e o mvc faz a substituição do {id} por tutorId.
        o tutor que será usado como resposta dentro do metodo getTutor no controler é setado aqui -> when(tutorUseCase.getTutor(tutorId)).thenReturn(tutor);
        após isso, o getTutorResponse é transformado em JSON via o response http e eu faço o expect de cada campo json
        */
    }

    @Test
    public void getAll() throws Exception {
        List<Tutor> tutors = new ArrayList<>();
        tutors.add(new Tutor("renato1", new PhoneNumber("111345678"), "rua1", OffsetDateTime.now(), OffsetDateTime.now()));
        tutors.add(new Tutor("renato2", new PhoneNumber("222345678"), "rua2", OffsetDateTime.now(), OffsetDateTime.now()));
        tutors.add(new Tutor("renato3", new PhoneNumber("333345678"), "rua3", OffsetDateTime.now(), OffsetDateTime.now()));
        tutors.add(new Tutor("renato4", new PhoneNumber("444345678"), "rua4", OffsetDateTime.now(), OffsetDateTime.now()));
    
        when(tutorUseCase.getAll()).thenReturn(tutors);

        mockMvc.perform(get("/tutor/all").
        contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(4))) 

        .andExpect(jsonPath("$[0].name").value("renato1"))
        .andExpect(jsonPath("$[0].phoneNumber").value("111345678"))
        .andExpect(jsonPath("$[0].address").value("rua1"))

        .andExpect(jsonPath("$[1].name").value("renato2"))
        .andExpect(jsonPath("$[1].phoneNumber").value("222345678"))
        .andExpect(jsonPath("$[1].address").value("rua2"))

        .andExpect(jsonPath("$[2].name").value("renato3"))
        .andExpect(jsonPath("$[2].phoneNumber").value("333345678"))
        .andExpect(jsonPath("$[2].address").value("rua3"))

        .andExpect(jsonPath("$[3].name").value("renato4"))
        .andExpect(jsonPath("$[3].phoneNumber").value("444345678"))
        .andExpect(jsonPath("$[3].address").value("rua4"));

        verify(tutorUseCase, times(1)).getAll();
        
        // O símbolo $ é o ponto de entrada para o documento JSON, ou seja, ele representa a raiz do JSON
    }

    @Test
    public void deleteTutor() throws Exception {
        Long tutorId = 1L;
        
        doNothing().when(tutorUseCase).delete(tutorId);

        mockMvc.perform(delete("/tutor/{id}", tutorId))
            .andExpect(status().isNoContent()); 

        verify(tutorUseCase, times(1)).delete(tutorId);
    }

    @Test
    public void testGetTutor_NotFound_ShouldReturn404() throws Exception {
        Long tutorId = 99L;

        when(tutorUseCase.getTutor(tutorId))
            .thenThrow(new TutorNotFoundException("Tutor with id " + tutorId + " not found"));

        mockMvc.perform(get("/tutor/{id}", tutorId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TUTOR_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Tutor with id 99 not found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/tutor/" + tutorId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).getTutor(tutorId);
    }

    @Test
    public void testAddTutor_ErrorDuplicatedPhone_ShouldReturn409() throws Exception {
        when(tutorUseCase.execute(any(AddTutorCommand.class)))
            .thenThrow(new DuplicatedPhoneNumberException("Numero de celular já consta na base de dados"));

         String requestJson = """
                            {
                            "name": "Renato",
                            "phoneNumber": "123456789",
                            "address": "rua 1"
                            }
                            """;

        mockMvc.perform(post("/tutor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATED_PHONE"))
            .andExpect(jsonPath("$.message").value("Numero de celular já consta na base de dados"))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.path").value("/tutor"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).execute(any(AddTutorCommand.class));
    }

    @Test
    public void testAddTutor_ErrorDomainValidation_ShouldReturn422() throws Exception {
        when(tutorUseCase.execute(any(AddTutorCommand.class)))
            .thenThrow(new DomainValidationException("Nome do Tutor é obrigatório"));

         String requestJson = """
                            {
                            "name": "",
                            "phoneNumber": "123456789",
                            "address": "rua 1"
                            }
                            """;

        mockMvc.perform(post("/tutor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("DOMAIN_VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Nome do Tutor é obrigatório"))
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.path").value("/tutor"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).execute(any(AddTutorCommand.class));
    }

    
    @Test
    public void testUpdateTutor_NotFound_ShouldReturn404() throws Exception {
        Long tutorId = 99L;

        doThrow(new TutorNotFoundException("Tutor id: " + tutorId + " não encontrado"))
        .when(tutorUseCase)
        .update(eq(tutorId), any(UpdateTutorCommand.class));

        String requestJson = """
                            {
                            "name": "",
                            "phoneNumber": "123456789",
                            "address": "rua 2"
                            }
                            """;

        mockMvc.perform(put("/tutor/{id}", tutorId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TUTOR_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Tutor id: " + tutorId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/tutor/" + tutorId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).update(eq(tutorId), any(UpdateTutorCommand.class));
    }

    
    @Test
    public void testDeleteTutor_NotFound_ShouldReturn404() throws Exception {
        Long tutorId = 99L;

        doThrow(new TutorNotFoundException("Tutor id: " + tutorId + " não encontrado"))
        .when(tutorUseCase)
        .delete(tutorId);

        mockMvc.perform(delete("/tutor/{id}", tutorId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TUTOR_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Tutor id: " + tutorId + " não encontrado"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.path").value("/tutor/" + tutorId))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(tutorUseCase, times(1)).delete(tutorId);
    }
}




/*
@ExtendWith(MockitoExtension.class):
O que faz: Essa anotação é usada para estender o suporte do JUnit 5 com o Mockito, permitindo o uso das funcionalidades do Mockito (como @Mock, @InjectMocks e outras) dentro de testes com o JUnit 5.
Por que é útil: O JUnit 5 precisa dessa anotação para integrar o Mockito corretamente, permitindo que você crie mocks (objetos simulados) de dependências e gerencie sua injeção de forma automatizada. Sem isso, você não poderia usar @Mock ou @InjectMocks em testes com JUnit 5.

@WebMvcTest(TutorController.class):
O que faz: Esta anotação é usada para criar um contexto de teste especializado para os controladores MVC do Spring. Ela carrega apenas os componentes necessários para testar o controlador (TutorController no seu caso), sem carregar toda a aplicação.
Por que é útil: Ao usar @WebMvcTest, você está testando apenas o comportamento do seu controlador, sem carregar o contexto completo da aplicação (como repositórios ou serviços). Isso acelera os testes e foca nas rotas da web, tornando o teste mais rápido e específico.
O que é carregado: Apenas os beans necessários para o controlador, como o MockMvc, são carregados. Qualquer dependência que não seja um componente web (como repositórios ou serviços) precisa ser mockada.

@Autowired
O que faz: A anotação @Autowired é usada para injetar automaticamente dependências no Spring. No contexto dos testes, ela é usada para injetar o objeto MockMvc, que permite simular requisições HTTP e testar o controlador.
Por que é útil: Ao usar @Autowired no MockMvc, você consegue realizar chamadas de API simuladas e verificar se os controladores respondem conforme esperado. O MockMvc facilita a interação com o controlador sem a necessidade de um servidor HTTP real.

@Mock
O que faz: A anotação @Mock é usada para criar objetos simulados (mocks) de classes ou interfaces.
Por que é útil: Ao criar um mock, você pode definir um comportamento simulado (resposta predefinida) para as chamadas feitas durante o teste, sem precisar de uma implementação real. Isso ajuda a isolar o controlador de outras partes do código, focando apenas na lógica do controlador.

mockMvc.perform(post("/tutor")...)
O que faz: Esse código realiza uma requisição HTTP do tipo POST para o endpoint /tutor, com um corpo JSON que contém os dados necessários. O mockMvc simula a requisição e retorna a resposta.
Por que é útil: Essa simulação permite que você envie uma requisição HTTP para o controlador sem precisar de um servidor real. Você pode verificar se o controlador está manipulando as requisições corretamente, retornando o status e os dados esperados.


Quando eu uso .thenReturn(meu_result): O controller PRECISA receber esse objeto pronto, porque ele usa o result para montar o response.

Se o objeto influencia o JSON da resposta → crie ele no teste.
Se o objeto só é usado internamente → use any() no mock.

Em alguns casos, o command nao tem uma função essencial para o teste, porque ele é usado para executar e a execução retorna um result.
Porem como eu estou simulando a execução eu nao preciso do command em si. Eu ja monto um result no teste que são dos dados que eu espero e passo pro retorno da
execução pelo thenReturn(result);
Já o result é importante, pois o response é criado a partir dele, por isso eu tenho que fazer o result.
O execute() eu posso executar com qualquer command - (Mockito.any(AddProfessionalCommand.class))

*/