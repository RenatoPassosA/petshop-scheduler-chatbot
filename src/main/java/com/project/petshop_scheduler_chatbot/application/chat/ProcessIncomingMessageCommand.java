package com.project.petshop_scheduler_chatbot.application.chat;

public class ProcessIncomingMessageCommand {
    private String waId;
    private String text;
    private String buttonId;
    private String phoneNumberId;

    public ProcessIncomingMessageCommand(String waId, String text, String buttonId, String phoneNumberId) {
        this.waId = waId;
        this.text = text;
        this.buttonId = buttonId;
        this.phoneNumberId = phoneNumberId;
    }

    public String getWaId() { 
        return waId;
    }

    public String getText() {
        return text;
    }

    public String getButtonId() {
        return buttonId;
    }

    public String getPhoneNumberId() { 
        return phoneNumberId;
    }
}

