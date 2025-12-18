package com.project.petshop_scheduler_chatbot.adapters.web.whatsapp.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WhatsAppWebhookPayload(List<Entry> entry) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(List<Change> changes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Change(Value value) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(Metadata metadata, List<Message> messages, List<Contact> contacts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(@JsonProperty("phone_number_id") String phoneNumberId) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contact(@JsonProperty("wa_id") String waId) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(@JsonProperty("from") String from, String type, Text text, Interactive interactive) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(String body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Interactive(@JsonProperty("button_reply") ButtonReply buttonReply, @JsonProperty("list_reply") ListReply listReply) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ButtonReply(String id, String title) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListReply(String id, String title) {}
}


/*
esse Payload é a forma como o wahtsapp envia pra mim a mensagem em JSON

ela vem mais ou menos desse jeito:
WebhookPayload
 └── entry[]
      └── changes[]
           └── value
                ├── metadata
                │    └── phone_number_id
                └── messages[]
                     ├── from
                     ├── type
                     ├── text
                     └── interactive
Cada nível existe porque:

o WhatsApp suporta múltiplos eventos no mesmo POST
múltiplas mensagens
múltiplas mudanças

Cada campo desse DTO  é uma classe (record), que contém seus próprios objetos, construtores, getters e setters com as infoirmações vindas do whatsapp*/
