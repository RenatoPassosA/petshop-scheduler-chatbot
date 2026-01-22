package com.project.petshop_scheduler_chatbot.infrastructure.messaging;

import java.util.List;

import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;

public class WhatsAppSendMessageRequest {

    private String messaging_product = "whatsapp";
    private String to;
    private String type;
    private Text text;
    private Interactive interactive;

    public static WhatsAppSendMessageRequest text(String to, String body) {
        WhatsAppSendMessageRequest req = new WhatsAppSendMessageRequest();
        req.to = to;
        req.type = "text";
        req.text = new Text(body);
        return req;
    }

    public static WhatsAppSendMessageRequest interactiveButtons(String to, String bodyText, List<ButtonOption> options) {
        WhatsAppSendMessageRequest req = new WhatsAppSendMessageRequest();
        req.to = to;
        req.type = "interactive";

        List<InteractiveActionButton> buttons = options.stream()
            .map(opt -> new InteractiveActionButton(
                new Reply(opt.id(), opt.title())
            ))
            .toList();

        req.interactive = new Interactive(
            "button",
            new Body(bodyText),
            Action.forButtons(buttons)
        );
        return req;
    }

    public static WhatsAppSendMessageRequest interactiveList(String to, String bodyText, String listButtonText, String sectionTitle, List<ButtonOption> options) {
        WhatsAppSendMessageRequest req = new WhatsAppSendMessageRequest();
        req.to = to;
        req.type = "interactive";

        List<Row> rows = options.stream()
            .map(opt -> new Row(opt.id(), opt.title(), null))
            .toList();

        Section section = new Section(sectionTitle, rows);

        req.interactive = new Interactive(
            "list",
            new Body(bodyText),
            Action.forList(listButtonText, List.of(section))
        );

        return req;
    }


    public static class Text {
        private String body;
        public Text(String body) { this.body = body; }
        public String getBody() { return body; }
    }

    public static class Interactive {
        private String type;
        private Body body;
        private Action action;

        public Interactive(String type, Body body, Action action) {
            this.type = type;
            this.body = body;
            this.action = action;
        }

        public String getType() { return type; }
        public Body getBody() { return body; }
        public Action getAction() { return action; }
    }

    public static class Body {
        private String text;
        public Body(String text) { this.text = text; }
        public String getText() { return text; }
    }

    public static class Section {
    private String title;
    private List<Row> rows;

    public Section(String title, List<Row> rows) {
        this.title = title;
        this.rows = rows;
    }

    public String getTitle() { return title; }
    public List<Row> getRows() { return rows; }
    }

    public static class Row {
        private String id;
        private String title;
        private String description; // opcional

        public Row(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }


    public static class Action {
        // BUTTONS
        private List<InteractiveActionButton> buttons;

        // LIST
        private String button;        // texto do botão que abre a lista
        private List<Section> sections;

        private Action() {}

        public static Action forButtons(List<InteractiveActionButton> buttons) {
            Action a = new Action();
            a.buttons = buttons;
            return a;
        }

        public static Action forList(String buttonText, List<Section> sections) {
            Action a = new Action();
            a.button = buttonText;
            a.sections = sections;
            return a;
        }

        public List<InteractiveActionButton> getButtons() { return buttons; }
        public String getButton() { return button; }
        public List<Section> getSections() { return sections; }
    }


    public static class InteractiveActionButton {
        private final String type = "reply";
        private Reply reply;

        public InteractiveActionButton(Reply reply) {
            this.reply = reply;
        }

        public String getType() { return type; }
        public Reply getReply() { return reply; }
    }

    public static class Reply {
        private String id;
        private String title;

        public Reply(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
    }

    public String getMessaging_product() { return messaging_product; }
    public String getTo() { return to; }
    public String getType() { return type; }
    public Text getText() { return text; }
    public Interactive getInteractive() { return interactive; }
}
