package com.project.petshop_scheduler_chatbot.application.chat;

public interface ChatMessagingPort {
    void sendTextMessage(String phoneNumber, String message);
    void sendTemplateMessage(String phoneNumber, String templateName, Object... variables);
}
