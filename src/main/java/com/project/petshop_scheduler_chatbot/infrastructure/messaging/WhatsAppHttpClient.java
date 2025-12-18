package com.project.petshop_scheduler_chatbot.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class WhatsAppHttpClient {

    private final RestTemplate restTemplate;

    @Value("${whatsapp.graph.base-url:https://graph.facebook.com}")
    private String baseUrl;

    @Value("${whatsapp.graph.version:v20.0}")
    private String version;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.token}")
    private String token;

    public WhatsAppHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(WhatsAppSendMessageRequest payload) {
        String url = baseUrl + "/" + version + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<WhatsAppSendMessageRequest> request = new HttpEntity<>(payload, headers);

        // logs (debug)
        System.out.println("\n=== WHATSAPP OUTGOING ===");
        System.out.println("URL: " + url);
        System.out.println("phoneNumberId: " + phoneNumberId);
        System.out.println("token(ending): " + safeTokenEnding(token));
        System.out.println("payload.to: " + payload.getTo()); // precisa existir no seu request
        System.out.println("payload.type: " + payload.getType()); // precisa existir no seu request

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            System.out.println("STATUS: " + response.getStatusCode());
            System.out.println("BODY: " + response.getBody());
            System.out.println("=== END WHATSAPP OUTGOING ===\n");

        } catch (HttpStatusCodeException e) {
            // cai aqui em 4xx/5xx e você consegue ver o JSON de erro do Graph
            System.out.println("STATUS: " + e.getStatusCode());
            System.out.println("ERROR BODY: " + e.getResponseBodyAsString());
            System.out.println("=== END WHATSAPP OUTGOING ===\n");
            throw e;
        }
    }

    private String safeTokenEnding(String token) {
        if (token == null) return "<null>";
        int len = token.length();
        return len <= 8 ? token : "..." + token.substring(len - 8);
    }
}
