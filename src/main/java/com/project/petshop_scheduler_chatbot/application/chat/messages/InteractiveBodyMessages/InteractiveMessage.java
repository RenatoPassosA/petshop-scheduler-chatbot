package com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages;

import java.util.List;

public final class InteractiveMessage {

    public enum Kind { BUTTONS, LIST }
    private final Kind kind;
    private final String body;
    private final List<ButtonOption> buttons;
    private final String listButtonText;
    private final String listSectionTitle;
    private final List<ButtonOption> rows;

    
    public InteractiveMessage(String body, List<ButtonOption> buttons) {
        this.kind = Kind.BUTTONS;
        this.body = body;
        this.buttons = buttons;

        this.listButtonText = null;
        this.listSectionTitle = null;
        this.rows = null;
    }

    private InteractiveMessage(
        Kind kind,
        String body,
        List<ButtonOption> buttons,
        String listButtonText,
        String listSectionTitle,
        List<ButtonOption> rows
    ) {
        this.kind = kind;
        this.body = body;
        this.buttons = buttons;
        this.listButtonText = listButtonText;
        this.listSectionTitle = listSectionTitle;
        this.rows = rows;
    }

    public static InteractiveMessage list(
        String body,
        String listButtonText,
        String sectionTitle,
        List<ButtonOption> rows
    ) {
        return new InteractiveMessage(Kind.LIST, body, null, listButtonText, sectionTitle, rows);
    }

    public Kind kind() { return kind; }
    public String body() { return body; }

    public List<ButtonOption> buttons() { return buttons; }

    public String listButtonText() { return listButtonText; }
    public String listSectionTitle() { return listSectionTitle; }
    public List<ButtonOption> rows() { return rows; }
}
