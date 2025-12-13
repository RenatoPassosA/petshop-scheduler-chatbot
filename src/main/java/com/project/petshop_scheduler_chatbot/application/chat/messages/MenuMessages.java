package com.project.petshop_scheduler_chatbot.application.chat.messages;

import java.util.List;

import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.ButtonOption;
import com.project.petshop_scheduler_chatbot.application.chat.messages.InteractiveBodyMessages.InteractiveMessage;

public class MenuMessages {
    public static InteractiveMessage noRegisteredMenu() {
        return new InteractiveMessage(
            "👋 Olá! Eu sou o assistente virtual do PetShop. Como posso te ajudar hoje?",
            List.of(new ButtonOption("REGISTER_TUTOR", "Cadastrar tutor"),
                    new ButtonOption("CHECK_SERVICES", "Ver serviços"),
                    new ButtonOption("TALK_TO_HUMAN", "Falar com atendente")
            ));
        }

    public static InteractiveMessage mainMenu(String name) {
        return new InteractiveMessage("Oi " + name + " ! 🐶\n O que você deseja fazer hoje?",
        List.of(new ButtonOption("SCHEDULE", "Marcar serviço"),
                    new ButtonOption("RESCHEDULE", "Reagendar serviço"),
                    new ButtonOption("CANCEL_SCHEDULE", "Cancelar serviço"),
                    new ButtonOption("REGISTER_PET", "Cadastrar outro pet"),
                    new ButtonOption("CHECK_SERVICES", "Ver serviços e preços"),
                    new ButtonOption("TALK_TO_HUMAN", "Falar com atendente")));
    }
}