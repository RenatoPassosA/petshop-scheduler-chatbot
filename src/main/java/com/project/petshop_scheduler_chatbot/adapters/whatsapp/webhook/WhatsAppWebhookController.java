package com.project.petshop_scheduler_chatbot.adapters.whatsapp.webhook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/whatsapp/webhook")
public class WhatsAppWebhookController {

    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam(name = "hub.mode", required = false) String mode,
                                                @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
                                                @RequestParam(name = "hub.challenge", required = false) String challenge) {
        String myVerifyToken = System.getenv("WHATSAPP_VERIFY_TOKEN");

        if ("subscribe".equals(mode) && myVerifyToken.equals(verifyToken)) 
            return ResponseEntity.ok(challenge);
        else 
            return ResponseEntity.status(403).body("Verification failed");  
    }

    /*
    Um webhook é um mecanismo de comunicação entre sistemas que envia dados automaticamente entre aplicações via HTTP, geralmente acionado por eventos específicos

    A documentação trata a forma como esse get deve ser feito:
    Sample Verification Request
    GET https://www.your-clever-domain-name.com/webhooks? -> meu link ngrok
    hub.mode=subscribe&
    hub.challenge=1158201444&
    hub.verify_token=meatyhamhock
    
    Validating Verification Requests
    Whenever your endpoint receives a verification request, it must:

    Verify that the hub.verify_token value matches the string you set in the Verify Token field when you configure the Webhooks product in your App Dashboard (you haven't set up this token string yet).
    Respond with the hub.challenge value.

    mode = operação que o meta está faznedo com o meu webhook. subscribe = inscrever o webhook. É o modo usado para validar que a URL realmente é sua
    verify_token = é exatamente o token secreto que você escolheu.
    challenge = é um número aleatório gerado pelo Meta onde ele exige que VOCÊ devolva esse mesmo número.
        assim que eles têm certeza de que: sua URL está online, você tem controle do servidor, você implementou o webhook corretamente
    */

    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody String body) {

        System.out.println("Recebido do WhatsApp:");
        System.out.println(body);

        return ResponseEntity.ok().build();
    }
}