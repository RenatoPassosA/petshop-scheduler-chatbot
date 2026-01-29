package com.project.petshop_scheduler_chatbot.application.chat.impl.handlers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
// import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorCommand;
import com.project.petshop_scheduler_chatbot.application.tutor.AddTutorResult;
import com.project.petshop_scheduler_chatbot.application.tutor.TutorUseCase;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;
import com.project.petshop_scheduler_chatbot.core.domain.valueobject.PhoneNumber;

@Component
public class RegisterTutorHandler {

    private final TutorUseCase tutorUseCase;
    private final StartMenuHandler startMenuHandler;
    private final RegisterPetHandler registerPetHandler;

    public RegisterTutorHandler(TutorUseCase tutorUseCase, StartMenuHandler startMenuHandler, RegisterPetHandler registerPetHandler) {
        this.tutorUseCase = tutorUseCase;
        this.startMenuHandler = startMenuHandler;
        this.registerPetHandler = registerPetHandler;
    }
    
    public ProcessIncomingMessageResult handle_STATE_REGISTER_TUTOR_START(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_STATE_REGISTER_TUTOR_START(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return startMenuHandler.STATE_START_handler(conversationSession);
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);
        return ProcessIncomingMessageResult.text("Informe o seu nome:");
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_TUTOR_NAME(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);
            return ProcessIncomingMessageResult.text("Por favor, digite um nome válido");
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
        conversationSession.setTempTutorName(messageCommand.getText());
        return ProcessIncomingMessageResult.text("Informe o seu endereço:");
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_TUTOR_ADDRESS(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_TextNullOrBlank(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_ADDRESS);
            return ProcessIncomingMessageResult.text("Por favor, digite um endereço válido");
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
        conversationSession.setTempTutorAddress(messageCommand.getText());
        return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, false),
                                                                                    List.of(new ButtonOption("YES", "SIM"),
                                                                                        new ButtonOption("NO", "NÃO"))));
    }


    public ProcessIncomingMessageResult handle_STATE_REGISTER_TUTOR_CONFIRM(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_YES_or_NO(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_CONFIRM);
            return ProcessIncomingMessageResult.interactive(new InteractiveMessage(generateConfirmationMessage(conversationSession, true),
                                                                                    List.of(new ButtonOption("YES", "SIM"),
                                                                                        new ButtonOption("NO", "NÃO"))));
        }

        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.resetFlowData();
            conversationSession.setCurrentState(ConversationState.STATE_START);
            return startMenuHandler.STATE_START_handler(conversationSession);
        }

        PhoneNumber phone = new PhoneNumber(conversationSession.getWaId());
        AddTutorCommand command = new AddTutorCommand(conversationSession.getTempTutorName(), phone, conversationSession.getTempTutorAddress());
        AddTutorResult result = tutorUseCase.execute(command);
        conversationSession.setTutorId(result.getTutorId());
        conversationSession.setRegisteredTutorName(conversationSession.getTempTutorName());
        conversationSession.resetFlowData();
        // String name = conversationSession.getTempTutorName();
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_AFTER_TUTOR);
        // return ProcessIncomingMessageResult.text("Agradecemos a preferencia!"); 
        return ProcessIncomingMessageResult.interactiveWithMessage("Agradecemos a preferencia!\n\n", new InteractiveMessage("Deseja cadastrar um pet?\n",
                                                                                                                                List.of(new ButtonOption("REGISTER_PET", "SIM"),
                                                                                                                                    new ButtonOption("NO", "NÃO"))));
    }

    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_AFTER_TUTOR(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError_REGISTER_PET_OR_NO(messageCommand)) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_AFTER_TUTOR);
            return ProcessIncomingMessageResult.interactiveWithMessage("Agradecemos a preferencia!\n\n", new InteractiveMessage("Deseja cadastrar um pet?\n",
                                                                                                                                List.of(new ButtonOption("REGISTER_PET", "SIM"),
                                                                                                                                    new ButtonOption("NO", "NÃO"))));
        }

        if ("NO".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_START);
            conversationSession.resetFlowData();
            return startMenuHandler.STATE_START_handler(conversationSession);
        }
        conversationSession.setCurrentState(ConversationState.STATE_REGISTER_PET_START);
        return registerPetHandler.handle_STATE_REGISTER_PET_START(conversationSession, messageCommand);
    }

    private boolean checkError_YES_or_NO(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"YES".equals(id) &&!"NO".equals(id)))  
            return true;
        return false;
    }

    private boolean checkError_REGISTER_PET_OR_NO(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || (!"REGISTER_PET".equals(id) && !"NO".equals(id)))
            return true;
        return false;
    }

    private boolean checkError_STATE_REGISTER_TUTOR_START(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || !"REGISTER_TUTOR".equals(id))
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

    private String  generateConfirmationMessage(ConversationSession conversationSession, boolean withError) {
        PhoneNumber phone = new PhoneNumber(conversationSession.getWaId());
        String message;
        if (withError)
            message = "⚠️ Não entendi, selecione SIM ou NÃO.\n" + //
                    "Os dados estão corretos?\nNome:" + 
                    conversationSession.getTempTutorName().trim() + 
                    "\nTelefone: " + 
                    phone.value() + 
                    "\nEndereço: " + 
                    conversationSession.getTempTutorAddress().trim();
        else
            message = "Os dados estão corretos?\nNome:" + 
                    conversationSession.getTempTutorName().trim() + 
                    "\nTelefone: " + 
                    phone.value() + 
                    "\nEndereço: " + 
                    conversationSession.getTempTutorAddress().trim();
        return message;
    }
}