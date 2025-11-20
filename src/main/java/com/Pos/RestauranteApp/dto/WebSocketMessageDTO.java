package com.Pos.RestauranteApp.dto;

public class WebSocketMessageDTO {
    private String type; 
    private Object payload; 

    public WebSocketMessageDTO(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }
    // Getters y Setters
    public String getType() { return type; }
    public Object getPayload() { return payload; }
}