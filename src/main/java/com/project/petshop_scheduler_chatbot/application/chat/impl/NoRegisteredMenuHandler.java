package com.project.petshop_scheduler_chatbot.application.chat.impl;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.impl.utils.ServicesFormatedList;
import com.project.petshop_scheduler_chatbot.application.chat.messages.MenuMessages;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;
import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationState;

public class NoRegisteredMenuHandler {

    private final ServicesFormatedList servicesFormatedList;

    public NoRegisteredMenuHandler(ServicesFormatedList servicesFormatedList) {
        this.servicesFormatedList = servicesFormatedList;
    }

    public ProcessIncomingMessageResult STATE_NO_REGISTERED_MENU_handler(ConversationSession conversationSession, ProcessIncomingMessageCommand messageCommand) {
        if (checkError(messageCommand))
            return ProcessIncomingMessageResult.interactiveWithMessage("⚠️ Opa! Não entendi sua escolha.\n\n", MenuMessages.noRegisteredMenu());

        if ("REGISTER_TUTOR".equals(messageCommand.getButtonId())) {
            conversationSession.setCurrentState(ConversationState.STATE_REGISTER_TUTOR_NAME);
            return ProcessIncomingMessageResult.text("Primeiro, me diga seu nome completo.");
        }
        else if ("CHECK_SERVICES".equals(messageCommand.getButtonId())) {
            String servicesList = servicesFormatedList.getAllServicesFormated();
            conversationSession.setCurrentState(ConversationState.STATE_CHECK_SERVICES);
            return ProcessIncomingMessageResult.text(servicesList);
        }
        else {
            conversationSession.setCurrentState(ConversationState.STATE_CHAT_WITH_HUMAN);
            conversationSession.setChatWithHuman(true);
            return ProcessIncomingMessageResult.text("Aguarde um instante, você já será atendido.");
        }
    }

    private boolean checkError(ProcessIncomingMessageCommand messageCommand) {
        String id = messageCommand.getButtonId();
        if (id == null || 
        (!"REGISTER_TUTOR".equals(id) &&  
        !"CHECK_SERVICES".equals(id) && 
        !"TALK_TO_HUMAN".equals(id)))
            return true;
        return false;
    }
    
}


