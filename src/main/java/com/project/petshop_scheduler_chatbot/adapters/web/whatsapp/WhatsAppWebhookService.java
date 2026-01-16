package com.project.petshop_scheduler_chatbot.adapters.web.whatsapp;

import org.springframework.stereotype.Service;

import com.project.petshop_scheduler_chatbot.adapters.web.whatsapp.dto.WhatsAppWebhookPayload;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageCommand;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageResult;
import com.project.petshop_scheduler_chatbot.application.chat.ProcessIncomingMessageUseCase;
import com.project.petshop_scheduler_chatbot.application.chat.inbound.WhatsAppInbound;
import com.project.petshop_scheduler_chatbot.core.ports.ChatMessagingPort;

@Service
public class WhatsAppWebhookService {

    private final ProcessIncomingMessageUseCase processIncomingMessageUseCase;
    private final ChatMessagingPort chatMessagingPort;

    public WhatsAppWebhookService(ProcessIncomingMessageUseCase processIncomingMessageUseCase, ChatMessagingPort chatMessagingPort) {
        this.processIncomingMessageUseCase = processIncomingMessageUseCase;
        this.chatMessagingPort = chatMessagingPort;
    }

    public void handle(WhatsAppWebhookPayload payload) {
        System.out.println("=== ENTROU NO handle ===");//
        try {
            WhatsAppInbound inbound = WhatsAppPayloadExtractor.extract(payload);
            if (inbound == null)
                return;

            ProcessIncomingMessageCommand cmd = new ProcessIncomingMessageCommand(
                inbound.getWaId(),
                inbound.getText(),
                inbound.getButtonId(),
                inbound.getPhoneNumberId()
            );

            ProcessIncomingMessageResult result = processIncomingMessageUseCase.execute(cmd);
            System.out.println("---------------------------------------" + result.getInteractive());

            if (result != null) {
                chatMessagingPort.send(result, inbound.getWaId());
            }
            } catch (Exception e) {
            // loga e não derruba o webhook (senão o WhatsApp reenfileira e você duplica processamento)
            // System.out.println("Erro processando webhook: " + e.getMessage());
            System.out.println("Erro processando webhook:");
            e.printStackTrace();
        }
    }
}


/* aqui eu monto o command com os dados recebidos do extractor e chamo o usecase */