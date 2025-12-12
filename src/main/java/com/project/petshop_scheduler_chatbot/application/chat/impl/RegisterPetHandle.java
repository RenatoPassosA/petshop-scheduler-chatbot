package com.project.petshop_scheduler_chatbot.application.chat.impl;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;

import com.project.petshop_scheduler_chatbot.core.domain.chatbot.ConversationSession;

public class RegisterPetHandle {
    public ProcessIncomingMessageResult handle_STATE_REGISTER_PET_START(ConversationSession session, ProcessIncomingMessageCommand command) {
        return ProcessIncomingMessageResult.text("Você não tem nenhum pet cadastrado");
    }
    
}
