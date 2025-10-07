package com.Pos.RestauranteApp.dto;

public class CategoriaDTO {
    private Long idCategoria;
    private String nombre;
    private Long idCategoriaPadre; // puede ser null si no tiene padre

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
