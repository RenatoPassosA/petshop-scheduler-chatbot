package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.application.pet.AddPetToTutorCommand;
import com.project.petshop_scheduler_chatbot.application.pet.PetUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.Gender;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PetSize;

@Component
public class RegisterPetHandler {

    private final PetUseCase petUseCase;

    public RegisterPetHandler(PetUseCase petUseCase) {
        this.petUseCase = petUseCase;
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_REGISTER_PET_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return ProcessIncomingMessageResult.interactive(MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_NAME);
        return ProcessIncomingMessageResult.text("Informe o nome do seu pet:");
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_NAME(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_NAME);
            return ProcessIncomingMessageResult.text("Por favor, digite um nome válido");
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_GENDER);
        conversationSession.setTempPetName(messageCommand.getText());
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Informe o sexo do seu pet:",
                                                                            List.of(new ButtonOption("M", "Macho"),
                                                                                new ButtonOption("F", "Femea"))));
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_GENDER(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_REGISTER_PET_GENDER(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_GENDER);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Por favor, selecione uma das opções.\nInforme o sexo do seu pet:\n",
                                                                            List.of(new ButtonOption("M", "Macho"),
                                                                                new ButtonOption("F", "Femea"))));
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_SIZE);
        conversationSession.setTempPetGender(messageCommand.getButtonId());
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Informe o porte do seu pet:",
                                                                            List.of(new ButtonOption("SMALL", "Pequeno"),
                                                                                    new ButtonOption("MEDIUM", "Médio"),
                                                                                    new ButtonOption("LARGE", "Grande"))));
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_SIZE(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_REGISTER_PET_SIZE(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_SIZE);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage("Por favor, selecione uma das opções.\nInforme o porte do seu pet:",
                                                                            List.of(new ButtonOption("SMALL", "Pequeno"),
                                                                                 new ButtonOption("MEDIUM", "Médio"),
                                                                                 new ButtonOption("LARGE", "Grande"))));
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_BREED);
        conversationSession.setTempPetSize(messageCommand.getButtonId());
        return ProcessIncomingMessageResult.text("Informe a raça do seu pet:");
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_BREED(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_BREED);
            return ProcessIncomingMessageResult.text("Por favor, digite uma raça válida");
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_OBS);
        conversationSession.setTempPetBreed(messageCommand.getText());
        return ProcessIncomingMessageResult.text("Alguma observação importante com o seu pet? Doenças, Limitações, etc...");
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_OBS(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setTempPetObs("sem observações");
        }
        else
            conversationSession.setTempPetObs(messageCommand.getText());
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_CONFIRM);
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, false),
                                                                            List.of(new ButtonOption("YES", "SIM"),
                                                                                    new ButtonOption("NO", "NÃO"))));
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_YES_or_NO(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_CONFIRM);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, true),
                                                                            List.of(new ButtonOption("YES", "SIM"),
                                                                                    new ButtonOption("NO", "NÃO"))));
        }

        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_MAIN_MENU);
            return ProcessIncomingMessageResult.interactive(MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
        }

        AddPetToTutorCommand command = new AddPetToTutorCommand(conversationSession.getTempPetName(),
                        getGender(conversationSession), 
                        getSize(conversationSession),
                        conversationSession.getTempPetBreed(),
                        conversationSession.getTutorId(),
                        conversationSession.getTempPetObs());
        petUseCase.execute(command);
        conversationSession.resetFlowData();
        conversationSession.setCurrentState(ConversationState.STATE_START);
        // return ProcessIncomingMessageResult.text("Agradecemos a preferencia!\nEstamos aguardando o seu pet!");
        return ProcessIncomingMessageResult.interactiveWithMessage("Agradecemos a preferencia!\n\n", MenuMessages.mainMenu(conversationSession.getRegisteredTutorName()));
    }

    private boolean checkError_STATE_REGISTER_PET_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"REGISTER_PET".equals(id))
            return true;
        return false;
    }

    private boolean checkError_TextNullOrBlank(ProcessIncomingMessageCommand messageCommand) {
        String text = messageCommand.getText();
        if (text == null)
            return true;
        text = text.trim();
        if (text.isBlank())
            return true;
        return false;
    }

    private boolean checkError_YES_or_NO(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
            return true;
        return false;
    }

    private boolean checkError_STATE_REGISTER_PET_GENDER(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"F".equals(id) &&!"M".equals(id)))  
            return true;
        return false;
    }

    

    private boolean checkError_STATE_REGISTER_PET_SIZE(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"SMALL".equals(id) && !"MEDIUM".equals(id) && !"LARGE".equals(id)))  
            return true;
        return false;
    }

    private String  generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        String message;
        if (withError)
            message = "⚠️ Selecione SIM ou NÃO\n\nOs dados estão corretos?\nNome:" + 
                        conversationSession.getTempPetName().trim() + 
                        "\nGenero: " + 
                        conversationSession.getTempPetGender() + 
                        "\nPorte: " + 
                        getStringSize(conversationSession) + 
                        "\nRaça: " +
                        conversationSession.getTempPetBreed() + 
                        "\nObservações: " +
                        conversationSession.getTempPetObs();
        else
            message = "Os dados estão corretos?\nNome:" + 
                        conversationSession.getTempPetName().trim() + 
                        "\nGenero: " + 
                        conversationSession.getTempPetGender() + 
                        "\nPorte: " + 
                        getStringSize(conversationSession) + 
                        "\nRaça: " +
                        conversationSession.getTempPetBreed() + 
                        "\nObservações: " +
                        conversationSession.getTempPetObs();
        return message;
    }    

    private Gender getGender (ConversationSession conversationSession) {
        if (conversationSession.getTempPetGender().equals("M"))
            return Gender.M;
        else
            return Gender.F;
    }

    private PetSize getSize (ConversationSession conversationSession) {
        if (conversationSession.getTempPetSize().equals("SMALL"))
            return PetSize.SMALL;
        else if (conversationSession.getTempPetSize().equals("MEDIUM"))
            return PetSize.MEDIUM;
        else
            return PetSize.LARGE;
    }

    private String getStringSize (ConversationSession conversationSession) {
        if (conversationSession.getTempPetSize().equals("SMALL"))
            return "Pequeno";
        else if (conversationSession.getTempPetSize().equals("MEDIUM"))
            return "Médio";
        else
            return "Grande";
    }
}
