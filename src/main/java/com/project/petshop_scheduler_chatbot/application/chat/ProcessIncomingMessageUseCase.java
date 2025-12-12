package com.project.petshop_scheduler_chatbot.application.chat;

public interface ProcessIncomingMessageUseCase {
    ProcessIncomingMessageResult execute(ProcessIncomingMessageCommand command); 
}
