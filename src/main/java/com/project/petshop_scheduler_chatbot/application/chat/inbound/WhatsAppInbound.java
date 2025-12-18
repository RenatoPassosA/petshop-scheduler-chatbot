package com.project.petshop_scheduler_chatbot.application.chat.inbound;

public class WhatsAppInbound {
    private final String waId;
    private final String phoneNumberId;
    private final String text;
    private final String buttonId;

    public WhatsAppInbound(String waId, String phoneNumberId, String text, String buttonId) {
        this.waId = waId;
        this.phoneNumberId = phoneNumberId;
        this.text = text;
        this.buttonId = buttonId;
    }

    public String getWaId() {
        return waId;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public String getText() {
        return text;
    }

    public String getButtonId() {
        return buttonId;
    }
}

/*esse DTO representa a mensagem vinda do wpp via JSON, já interpretada pro meu sistema, que será enviada para o usecase*/