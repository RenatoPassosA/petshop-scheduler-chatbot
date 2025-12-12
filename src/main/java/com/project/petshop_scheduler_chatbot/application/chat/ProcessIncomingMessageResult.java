package com.project.petshop_scheduler_chatbot.application.chat;

import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;

public class ProcessIncomingMessageResult {

    public enum Kind {
        TEXT,
        INTERACTIVE
    }

    private final Kind type;
    private final String text;
    private final InteractiveMessage interactive;

    private ProcessIncomingMessageResult(Kind type, String text, InteractiveMessage interactive) {
        this.type = type;
        this.text = text;
        this.interactive = interactive;
    }

    public static ProcessIncomingMessageResult text(String message) {
        return new ProcessIncomingMessageResult(Kind.TEXT, message, null);
    }

    public static ProcessIncomingMessageResult interactive(InteractiveMessage message) {
        return new ProcessIncomingMessageResult(Kind.INTERACTIVE, null, message);
    }

    public static ProcessIncomingMessageResult interactiveWithMessage(String text, InteractiveMessage message) {
        return new ProcessIncomingMessageResult(Kind.INTERACTIVE, text, message);
    }

    public Kind getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public InteractiveMessage getInteractive() {
        return interactive;
    }
}
