package com.Pos.RestauranteApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilita un "broker" (centro de mensajes) en memoria.
        // Los clientes (frontend) se suscribirán a destinos que comiencen con "/topic"
        registry.enableSimpleBroker("/topic");
        
        // Define el prefijo para los mensajes que van del cliente al servidor (no lo usaremos mucho aquí)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define el endpoint HTTP que el cliente usará para conectarse al WebSocket.
        // "/ws" es la URL de conexión (ej. ws://localhost:8080/ws)
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*"); // Permite conexiones de cualquier origen
    }
}