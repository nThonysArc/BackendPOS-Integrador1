package com.Pos.RestauranteApp.dto.auth;

// NO ES NECESARIO MODIFICAR ESTE ARCHIVO SI YA LO TIENES ASÍ
public class AuthResponse {
    private String token;
    
    // --- AÑADIR ESTOS CAMPOS ---
    private Long id;
    private String nombre;
    private String rol;

    // --- MODIFICAR CONSTRUCTOR ---
    public AuthResponse(String token, Long id, String nombre, String rol) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    // --- AÑADIR GETTERS Y SETTERS PARA LOS NUEVOS CAMPOS ---
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}