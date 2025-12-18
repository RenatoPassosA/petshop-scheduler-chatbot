package com.project.petshop_scheduler_chatbot.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;
import com.project.petshop_scheduler_chatbot.core.ports.ChatMessagingPort;

@Component
public class WhatsAppMessagingPortAdapter implements ChatMessagingPort {

    private final WhatsAppHttpClient whatsAppHttpClient;

    public WhatsAppMessagingPortAdapter(WhatsAppHttpClient whatsAppHttpClient) {
        this.whatsAppHttpClient = whatsAppHttpClient;
    }

    @Override
    public void send(ProcessIncomingMessageResult result, String waId) {
        if (result == null) return;

        switch (result.getType()) {
            case TEXT -> sendText(result, waId);
            case INTERACTIVE -> sendInteractive(result, waId);
            default -> throw new IllegalArgumentException("Tipo de mensagem não suportado: " + result.getType());
        }
    }

    private void sendText(ProcessIncomingMessageResult result, String waId) {
        String text = result.getText();
        if (text == null || text.isBlank()) return;

        whatsAppHttpClient.sendMessage(
            WhatsAppSendMessageRequest.text(waId, text)
        );
    }

    private void sendInteractive(ProcessIncomingMessageResult result, String waId) {
        InteractiveMessage interactive = result.getInteractive();
        if (interactive == null) return;

        String prefix = (result.getText() != null && !result.getText().isBlank())
            ? result.getText().trim() + "\n"
            : "";

        String body = prefix + interactive.body();

        whatsAppHttpClient.sendMessage(
            WhatsAppSendMessageRequest.interactiveButtons(waId, body, interactive.buttons())
        );
    }
}
