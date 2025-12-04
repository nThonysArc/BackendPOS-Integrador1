package com.Pos.RestauranteApp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException; 
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
        System.out.println(">>> Intentando enviar WhatsApp a: " + numeroMotorizado);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            
            // CORRECCIÓN: Aseguramos el formato E.164 (con +) para el API de WhatsApp
            String toNumero = numeroMotorizado.startsWith("+") ? numeroMotorizado : "+" + numeroMotorizado;
            
            body.put("recipient_type", "individual");
            body.put("to", toNumero); 
            body.put("type", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("body", mensajeTexto);
            body.put("text", textContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(apiUrl, request, String.class);
            System.out.println("✅ WhatsApp enviado EXITOSAMENTE al: " + toNumero); 

        } catch (HttpClientErrorException e) {
            System.err.println("❌ ERROR API FACEBOOK: " + e.getStatusCode());
            System.err.println("📩 DETALLE DEL ERROR: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("❌ Error General WhatsApp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}