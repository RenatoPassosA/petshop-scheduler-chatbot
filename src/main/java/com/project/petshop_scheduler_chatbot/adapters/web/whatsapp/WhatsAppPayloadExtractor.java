package com.project.petshop_scheduler_chatbot.adapters.web.whatsapp;

import com.project.petshop_scheduler_chatbot.adapters.web.whatsapp.dto.WhatsAppWebhookPayload;
import com.project.petshop_scheduler_chatbot.application.chat.inbound.WhatsAppInbound;

public final class WhatsAppPayloadExtractor {

    private WhatsAppPayloadExtractor() {}

    public static WhatsAppInbound extract(WhatsAppWebhookPayload payload) {
        System.out.println("[EXTRACTOR] Iniciando extração do payload");
        if (payload == null || payload.entry() == null || payload.entry().isEmpty()) return null;

        var entry = payload.entry().get(0);
        if (entry.changes() == null || entry.changes().isEmpty()) return null;

        var value = entry.changes().get(0).value();
        if (value == null || value.metadata() == null) return null;

        String phoneNumberId = value.metadata().phoneNumberId();

        if (value.messages() == null || value.messages().isEmpty()) return null;
        var msg = value.messages().get(0);

        String waId = msg.from();
        String text = null;
        String buttonId = null;

        if ("text".equals(msg.type()) && msg.text() != null) {
            text = msg.text().body();
        } else if ("interactive".equals(msg.type()) && msg.interactive() != null) {
            if (msg.interactive().buttonReply() != null) {
                buttonId = msg.interactive().buttonReply().id();
            } else if (msg.interactive().listReply() != null) {
                buttonId = msg.interactive().listReply().id();
            }
        }
        return new WhatsAppInbound(waId, phoneNumberId, text, buttonId);
    }
}

/*esse metodo somente extrai do JSON as informações que necessito (whatsapp id, texto ou botao selecionado) e retorna ele na forma do DTO que meu sistema entende*/
