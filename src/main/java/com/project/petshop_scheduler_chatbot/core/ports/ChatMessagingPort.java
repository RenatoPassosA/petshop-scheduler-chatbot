package com.project.petshop_scheduler_chatbot.core.ports;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;

public interface ChatMessagingPort {
    void send(ProcessIncomingMessageResult result, String waId);
}
