package com.project.petshop_scheduler_chatbot.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.petshop_scheduler_chatbot.application.chat.impl.handlers.RegisterPetHandler;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

@ExtendWith(MockitoExtension.class)
public class RegisterPetHandlerTest {

    @Mock
    PetUseCase petUseCase;

    private RegisterPetHandler registerPetHandler;

    @BeforeEach
    void setUp() {
        registerPetHandler = new RegisterPetHandler(petUseCase);
    }

    private ProcessIncomingMessageCommand generateMessageCommand(String text, String buttonId) {
        return new ProcessIncomingMessageCommand("21988398302", text, buttonId, "21988398302" );
    }
    
    @Test
    void start_InvalidButton_ShouldGoBackToStartMenu() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "INVALID");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_START);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_START);
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void start_ValidButton_REGISTER_PET_ShouldAskName() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "REGISTER_PET");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_START);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_START(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_NAME);
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Informe o nome do seu pet:");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void name_NullOrBlank_ShouldStayInNameAndReturnErrorText() {
        ProcessIncomingMessageCommand command1 = generateMessageCommand(null, null);
        ProcessIncomingMessageCommand command2 = generateMessageCommand("    ", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_NAME);

        ProcessIncomingMessageResult result1 = registerPetHandler.handle_STATE_REGISTER_PET_NAME(session, command1);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_NAME);
        assertThat(result1.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        assertThat(result1.getText()).contains("Por favor, digite um nome válido");

        ProcessIncomingMessageResult result2 = registerPetHandler.handle_STATE_REGISTER_PET_NAME(session, command2);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_NAME);
        assertThat(result2.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        assertThat(result2.getText()).contains("Por favor, digite um nome válido");
        verifyNoInteractions(petUseCase);
    }

    @Test
    void nameValidText_ShouldGoToGenderAndStoreTempName() {
        ProcessIncomingMessageCommand command = generateMessageCommand("Flor", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_NAME);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_NAME(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_GENDER);
        assertThat(session.getTempPetName()).isEqualTo("Flor");
        assertThat(result).isNotNull();
        assertThat(result.getInteractive().body()).contains("Informe o sexo do seu pet:");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void gender_InvalidButton_ShouldStayInGenderAndReturnErrorInteractive() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "X");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_GENDER);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_GENDER(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_GENDER);
        assertThat(result.getInteractive().body()).contains("Por favor, selecione uma das opções");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void genderValidButton_ShouldGoToSizeAndStoreTempGender() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "F");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_GENDER);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_GENDER(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_SIZE);
        assertThat(session.getTempPetGender()).isEqualTo("F");
        assertThat(result).isNotNull();
        assertThat(result.getInteractive().body()).contains("Informe o porte do seu pet:");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void size_InvalidButton_ShouldStayInSizeAndReturnErrorInteractive() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "AAAAAA");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_SIZE);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_SIZE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_SIZE);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Por favor, selecione uma das opções");
        verifyNoInteractions(petUseCase);
    }

    @Test
    void size_ValidButton_ShouldGoToBreedAndStoreTempSize() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "SMALL");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_SIZE);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_SIZE(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_BREED);
        assertThat(session.getTempPetSize()).isEqualTo("SMALL");
        assertThat(result.getText()).contains("Informe a raça do seu pet");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void breed_InvalidText_ShouldStayInBreedAndReturnErrorText() {
        ProcessIncomingMessageCommand command = generateMessageCommand("   ", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_BREED);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_BREED(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_BREED);
        assertThat(result.getText()).contains("Por favor, digite uma raça válida");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);

        verifyNoInteractions(petUseCase);
    }

    @Test
    void breed_ValidText_ShouldGoToObsAndStoreTempBreed() {
        ProcessIncomingMessageCommand command = generateMessageCommand("York", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_BREED);

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_BREED(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_OBS);
        assertThat(session.getTempPetBreed()).isEqualTo("York");
        assertThat(result.getText()).contains("Alguma observação importante");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.TEXT);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void obs_Blank_ShouldDefaultToSemObservacoes_AndGoToConfirm() {
        ProcessIncomingMessageCommand command = generateMessageCommand("   ", null);
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_OBS);

        session.setTempPetName("Flor");
        session.setTempPetGender("F");
        session.setTempPetSize("SMALL");
        session.setTempPetBreed("York");

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_OBS(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_CONFIRM);
        assertThat(session.getTempPetObs()).isEqualTo("sem observações");
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }

    @Test
    void confirm_YES_ShouldCallUseCase_ResetFlow_AndGoToStart() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "YES");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_CONFIRM);
        session.setTempPetName("Flor");
        session.setTempPetGender("F");
        session.setTempPetSize("SMALL");
        session.setTempPetBreed("York");
        session.setTutorId(1L);
        session.setTempPetObs("nenhuma");

        ProcessIncomingMessageResult result =
            registerPetHandler.handle_STATE_REGISTER_PET_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(session.getTempPetName()).isNull();
        assertThat(session.getTempPetGender()).isNull();
        assertThat(session.getTempPetSize()).isNull();
        assertThat(session.getTempPetBreed()).isNull();
        assertThat(session.getTempPetObs()).isNull();

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);

        String body = result.getInteractive() != null ? result.getInteractive().body() : "";
        String text = result.getText() != null ? result.getText() : "";
        assertThat(body + text).contains("Agradecemos a preferencia!");

        verify(petUseCase).execute(any(AddPetToTutorCommand.class));
    }


    @Test
    void confirm_InvalidButton_ShouldStayInConfirmAndReturnErrorInteractive() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "AAAA");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_CONFIRM);

        session.setTempPetName("Flor");
        session.setTempPetGender("F");
        session.setTempPetSize("SMALL");
        session.setTempPetBreed("Vira-lata");
        session.setTempPetObs("sem observações");

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_REGISTER_PET_CONFIRM);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        assertThat(result.getInteractive().body()).contains("Selecione SIM ou NÃO");
        verifyNoInteractions(petUseCase);
    }

    @Test
    void confirm_NO_ShouldGoToMainMenu_AndNotCallUseCase() {
        ProcessIncomingMessageCommand command = generateMessageCommand(null, "NO");
        ConversationSession session = new ConversationSession("21988398302");
        session.setCurrentState(ConversationState.STATE_REGISTER_PET_CONFIRM);

        session.setTempPetName("Flor");
        session.setTempPetGender("F");
        session.setTempPetSize("SMALL");
        session.setTempPetBreed("Vira-lata");
        session.setTempPetObs("sem observações");

        ProcessIncomingMessageResult result = registerPetHandler.handle_STATE_REGISTER_PET_CONFIRM(session, command);

        assertThat(session.getCurrentState()).isEqualTo(ConversationState.STATE_MAIN_MENU);
        assertThat(result.getType()).isEqualTo(ProcessIncomingMessageResult.Kind.INTERACTIVE);
        verifyNoInteractions(petUseCase);
    }
}
