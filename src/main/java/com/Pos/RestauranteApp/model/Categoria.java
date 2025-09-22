package com.Pos.RestauranteApp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Relación autorreferenciada (categoría padre)
    @ManyToOne
    @JoinColumn(name = "id_categoria_padre")
    private Categoria categoriaPadre;

    public Categoria() {}

    public Categoria(String nombre, Categoria categoriaPadre) {
        this.nombre = nombre;
        this.categoriaPadre = categoriaPadre;
    }

    // Getters y setters
    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Categoria getCategoriaPadre() { return categoriaPadre; }
    public void setCategoriaPadre(Categoria categoriaPadre) { this.categoriaPadre = categoriaPadre; }
}
