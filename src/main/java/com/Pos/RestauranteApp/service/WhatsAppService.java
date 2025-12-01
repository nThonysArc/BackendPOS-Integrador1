package com.Pos.RestauranteApp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.token}")
    private String apiToken;

    @Value("${whatsapp.motorizado.numero}")
    private String numeroMotorizado;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarMensajeAlMotorizado(String mensajeTexto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            body.put("recipient_type", "individual");
            body.put("to", numeroMotorizado);
            body.put("type", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("body", mensajeTexto);
            body.put("text", textContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            // Envío
            restTemplate.postForEntity(apiUrl, request, String.class);
            System.out.println("✅ WhatsApp enviado al: " + numeroMotorizado);

        } catch (Exception e) {
            System.err.println("❌ Error WhatsApp: " + e.getMessage());
        }
    }
}