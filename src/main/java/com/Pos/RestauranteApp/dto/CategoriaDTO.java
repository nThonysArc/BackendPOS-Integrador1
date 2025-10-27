package com.Pos.RestauranteApp.dto;

import jakarta.validation.constraints.NotBlank; 

public class CategoriaDTO {
    private Long idCategoria;

    @NotBlank(message = "El nombre no puede estar vacío") 
    private String nombre;

    private Long idCategoriaPadre; // puede ser null si no tiene padre

    // Constructor vacío (AÑADIDO por si no lo tenías)
    public CategoriaDTO() {}

    public CategoriaDTO(Long idCategoria, String nombre, Long idCategoriaPadre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.idCategoriaPadre = idCategoriaPadre;
    }

    // Getters y Setters
    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdCategoriaPadre() {
        return idCategoriaPadre;
    }

    public void setIdCategoriaPadre(Long idCategoriaPadre) {
        this.idCategoriaPadre = idCategoriaPadre;
    }
}