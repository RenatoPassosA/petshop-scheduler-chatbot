package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult.Kind;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterPetHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterTutorHandler;
import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.StartMenuHandler;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

@ExtendWith(MockitoExtension.class)
public class RegisterTutorHandlerTest {

    @Mock TutorUseCase tutorUseCase;
    @Mock StartMenuHandler startMenuHandler;
    @Mock RegisterPetHandler registerPetHandler;

    private RegisterTutorHandler registerTutorHandler;

    @BeforeEach
    void setUp() {
        registerTutorHandler = new RegisterTutorHandler(tutorUseCase, startMenuHandler, registerPetHandler);
    }

    private ProcessIncomingMessageCommand generateMessageCommand(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "21988398302" );
    }

    @Test
    void start_InvalidButton_ShouldGoBackToStartMenu() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_START);

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("expected");
        when(startMenuHandler.STATE_START_handler(session)).thenReturn(expected);


        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result).isEqualTo(expected);
        verify(startMenuHandler, times(1)).STATE_START_handler(session);
        verifyNoInteractions(tutorUseCase);
    }

    @Test
    void start_ValidButton_REGISTER_TUTOR_ShouldAskName() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "REGISTER_TUTOR");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_START);

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_NAME);
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result.getText()).contains("Informe o seu nome:");
        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerName_NameNullOrBlank_ShouldBacktoRegisterTutorAndAskName() {
        ProcessIncomingMessageCommand command1 = generateMessageCommand("   ", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);

        ProcessIncomingMessageResult result1 = registerTutorHandler.handle_STATE_REGISTER_TUTOR_NAME(session, command1);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_NAME);
        assertThat(result1.getType()).isEqualTo(Kind.TEXT);
        assertThat(result1.getText()).contains("Por favor, digite um nome válido");

        ProcessIncomingMessageCommand command2 = generateMessageCommand(null, null);
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);
        ProcessIncomingMessageResult result2 = registerTutorHandler.handle_STATE_REGISTER_TUTOR_NAME(session, command2);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_NAME);
        assertThat(result2.getType()).isEqualTo(Kind.TEXT);
        assertThat(result2.getText()).contains("Por favor, digite um nome válido");

        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerName_ValidName_ShouldAskTutorAddress() {
        ProcessIncomingMessageCommand command = generateMessageCommand("Renato", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_NAME(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
        assertThat(result.getType()).isEqualTo(Kind.TEXT);
        assertThat(result.getText()).contains("Informe o seu endereço:");
        assertThat(session.getTempTutorName()).isEqualTo("Renato");
        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerAddress_AddressNullOrBlank_ShouldAskTutorAddress() {
        ProcessIncomingMessageCommand command1 = generateMessageCommand("   ", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);

        ProcessIncomingMessageResult result1 = registerTutorHandler.handle_STATE_REGISTER_TUTOR_ADDRESS(session, command1);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
        assertThat(result1.getType()).isEqualTo(Kind.TEXT);
        assertThat(result1.getText()).contains("Por favor, digite um endereço válido");

        ProcessIncomingMessageCommand command2 = generateMessageCommand(null, null);
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
        ProcessIncomingMessageResult result2 = registerTutorHandler.handle_STATE_REGISTER_TUTOR_ADDRESS(session, command2);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
        assertThat(result2.getType()).isEqualTo(Kind.TEXT);
        assertThat(result2.getText()).contains("Por favor, digite um endereço válido");

        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerAddress_ValidAddress_ShouldAskForConfirmation() {
        ProcessIncomingMessageCommand command = generateMessageCommand("Rua 1", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setTempTutorName("Renato");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_ADDRESS(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Os dados estão corretos?");
        assertThat(session.getTempTutorAddress()).isEqualTo("Rua 1");
        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerConfirm_InvalidConfirmation_ShouldAskForConfirmation() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "AAAAA");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        session.setTempTutorName("Renato");
        session.setTempTutorAddress("Rua 1");

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("⚠️ Não entendi, selecione SIM ou NÃO.");
        verifyNoInteractions(tutorUseCase);
        verifyNoInteractions(startMenuHandler);
    }

    @Test
    void registerConfirm_NoConfirmation_ShouldResetDataAndDelegateToStateStart() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "NO");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        session.setTempTutorName("Renato");
        session.setTempTutorAddress("Rua 1");

        ProcessIncomingMessageResult expected = ProcessIncomingMessageResult.text("expected");

        when(startMenuHandler.STATE_START_handler(session)).thenReturn(expected);

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result).isEqualTo(expected);
        assertThat(session.getTempTutorName()).isNull();
        assertThat(session.getTempTutorAddress()).isNull();

        verifyNoInteractions(tutorUseCase);
        verify(startMenuHandler, times(1)).STATE_START_handler(session);;
    }

    @Test
    void registerConfirm_ValidConfirmation_ShouldResetDataSendConfirmationMessage() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "YES");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        session.setTempTutorName("Renato");
        session.setTempTutorAddress("Rua 1");

        ProcessIncomingMessageResult result = registerTutorHandler.handle_STATE_REGISTER_TUTOR_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result.getType()).isEqualTo(Kind.INTERACTIVE);
        String body = result.getInteractive() != null ? result.getInteractive().body() : "";
        String text = result.getText() != null ? result.getText() : "";
        assertThat(body + text).contains("Agradecemos a preferencia!");
        assertThat(session.getTempTutorName()).isNull();
        assertThat(session.getTempTutorAddress()).isNull();
        verify(tutorUseCase, times(1)).execute(any(AddTutorCommand.class));
        verifyNoInteractions(startMenuHandler);
    }

}