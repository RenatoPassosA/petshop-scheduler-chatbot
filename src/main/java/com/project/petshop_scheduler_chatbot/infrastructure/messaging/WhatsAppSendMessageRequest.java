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

    public static WhatsAppSendMessageRequest interactiveButtons(
        String to,
        String bodyText,
        List<ButtonOption> options
    ) {
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
            new Action(buttons)
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

    public static class Action {
        private List<InteractiveActionButton> buttons;
        public Action(List<InteractiveActionButton> buttons) {
            this.buttons = buttons;
        }
        public List<InteractiveActionButton> getButtons() { return buttons; }
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
