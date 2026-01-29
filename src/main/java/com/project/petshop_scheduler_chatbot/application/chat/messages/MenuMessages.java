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
        return new InteractiveMessage(
            "Oi " + name + " ! 🐶\nO que você deseja fazer hoje?",
            List.of(
                new ButtonOption("SCHEDULE", "Agendar serviço"),
                new ButtonOption("CHECK_SERVICES", "Serviços e preços"),
                new ButtonOption("MORE_OPTIONS", "Mais opções")
            ));
    }

    public static InteractiveMessage moreOptionsMenu() {
        return new InteractiveMessage(
            "",
            List.of(
                new ButtonOption("REGISTER_PET", "Cadastrar pet"),
                new ButtonOption("RESCHEDULE", "Reagendar serviço"),
                new ButtonOption("TALK_TO_HUMAN", "Falar com atendente")
            ));
    }

    public static InteractiveMessage  afterListServicesNoRegistered() {
        return new InteractiveMessage(
            "\nO que você deseja fazer hoje?",
            List.of(new ButtonOption("REGISTER_TUTOR", "Cadastrar tutor"),
                    new ButtonOption("CHECK_SERVICES", "Ver serviços"),
                    new ButtonOption("TALK_TO_HUMAN", "Falar com atendente")
            ));
    }

    public static InteractiveMessage afterListServicesRegistered(String name) {
        return new InteractiveMessage(
            "\nO que você deseja fazer hoje, " + name + "?",
            List.of(
                new ButtonOption("SCHEDULE", "Agendar serviço"),
                new ButtonOption("CHECK_SERVICES", "Serviços e preços"),
                new ButtonOption("MORE_OPTIONS", "Mais opções")
            ));
    }
}