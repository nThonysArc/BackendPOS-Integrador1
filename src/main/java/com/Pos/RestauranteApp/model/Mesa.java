package com.Pos.RestauranteApp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMesa;

    @Column(nullable = false) // Se quitó la propiedad unique 03/12/2025
    private Integer numeroMesa;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    // CAMPOS PARA REPRESENTACION VISUAL EN MAPA
    private Double posX = 0.0;
    private Double posY = 0.0;
    private Double width = 80.0;  // Ancho visual
    private Double height = 80.0; // Alto visual
    private Double rotation = 0.0;
    private String forma = "RECTANGLE"; // RECTANGLE, CIRCLE
    private String tipo = "MESA"; // MESA, OBSTACULO (Paredes, baños, etc.)
    // ------------------------------

    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PedidoMesa> pedidos;

    public enum EstadoMesa {
        DISPONIBLE, OCUPADA, RESERVADA, BLOQUEADA
    }

    // Getters y Setters completos (incluyendo los nuevos)
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }
    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }
    public List<PedidoMesa> getPedidos() { return pedidos; }
    public void setPedidos(List<PedidoMesa> pedidos) { this.pedidos = pedidos; }

    // Getters visuales
    public Double getPosX() { return posX; }
    public void setPosX(Double posX) { this.posX = posX; }
    public Double getPosY() { return posY; }
    public void setPosY(Double posY) { this.posY = posY; }
    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getRotation() { return rotation; }
    public void setRotation(Double rotation) { this.rotation = rotation; }
    public String getForma() { return forma; }
    public void setForma(String forma) { this.forma = forma; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}