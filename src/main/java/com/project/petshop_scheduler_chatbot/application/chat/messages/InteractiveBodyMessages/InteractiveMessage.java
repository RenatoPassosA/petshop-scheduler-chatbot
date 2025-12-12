package com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages;

import java.util.List;

public record InteractiveMessage(
    String body,
    List<ButtonOption> buttons
) {}
