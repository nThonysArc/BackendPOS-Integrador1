package com.Pos.RestauranteApp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMesa;

    @Column(nullable = false, unique = true)
    private Integer numeroMesa;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    private EstadoMesa estado = EstadoMesa.DISPONIBLE;

    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PedidoMesa> pedidos;

    public enum EstadoMesa {
        DISPONIBLE, OCUPADA, RESERVADA, BLOQUEADA
    }

    // Getters y setters

    public List<PedidoMesa> getPedidos() {return pedidos;}
    public void setPedidos(List<PedidoMesa> pedidos) {this.pedidos = pedidos;}

    public EstadoMesa getEstado() {return estado;}
    public void setEstado(EstadoMesa estado) {this.estado = estado;}

    public Integer getCapacidad() {return capacidad;}
    public void setCapacidad(Integer capacidad) {this.capacidad = capacidad;}

    public Integer getNumeroMesa() {return numeroMesa;}
    public void setNumeroMesa(Integer numeroMesa) {this.numeroMesa = numeroMesa;}

    public Long getIdMesa() {return idMesa;}
    public void setIdMesa(Long idMesa) {this.idMesa = idMesa;}
}
