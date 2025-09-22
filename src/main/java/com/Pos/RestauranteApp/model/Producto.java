package com.Pos.RestauranteApp.model;

import jakarta.persistence.*;

@Entity

@Table(name = "Productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idProducto;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Boolean activo = true;

    private long idCategoria;

    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(long idCategoria) { this.idCategoria= idCategoria; }
}