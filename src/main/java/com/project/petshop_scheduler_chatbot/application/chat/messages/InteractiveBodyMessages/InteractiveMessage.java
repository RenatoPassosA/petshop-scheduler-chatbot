package com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages;

import java.util.List;

public final class InteractiveMessage {

    public enum Kind { BUTTONS, LIST }

    private final Kind kind;

    // comum
    private final String body;

    // BUTTONS
    private final List<ButtonOption> buttons;

    // LIST
    private final String listButtonText;      // texto do botão que abre a lista
    private final String listSectionTitle;    // título da seção
    private final List<ButtonOption> rows;    // opções da lista (id + title)

    /**
     * ✅ Mantém compatibilidade com o que você já usa:
     * new InteractiveMessage(body, buttons)
     */
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

    /**
     * ✅ Novo: cria mensagem no formato LIST
     */
    public static InteractiveMessage list(
        String body,
        String listButtonText,
        String sectionTitle,
        List<ButtonOption> rows
    ) {
        return new InteractiveMessage(Kind.LIST, body, null, listButtonText, sectionTitle, rows);
    }

    // Mantendo "accessors" no estilo record (body(), buttons(), etc.)
    public Kind kind() { return kind; }
    public String body() { return body; }

    public List<ButtonOption> buttons() { return buttons; }

    public String listButtonText() { return listButtonText; }
    public String listSectionTitle() { return listSectionTitle; }
    public List<ButtonOption> rows() { return rows; }
}
