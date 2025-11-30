package com.Pos.RestauranteApp.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidosmesa")
public class PedidoMesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedidoMesa;

    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "id_mesero", nullable = false)
    private Empleado mesero;

    private LocalDateTime fechaHoraCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado = EstadoPedido.ABIERTO;

    private Double total = 0.0;

    @OneToMany(mappedBy = "pedidoMesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedidoMesa> detalles;

    public enum EstadoPedido {
        ABIERTO,
        EN_COCINA, 
        LISTO_PARA_ENTREGAR, 
        CERRADO,
        CANCELADO
    }

    // Getters y setters

    public Long getIdPedidoMesa() {
        return idPedidoMesa;
    }

    public void setIdPedidoMesa(Long idPedidoMesa) {
        this.idPedidoMesa = idPedidoMesa;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Empleado getMesero() {
        return mesero;
    }

    public void setMesero(Empleado mesero) {
        this.mesero = mesero;
    }

    public LocalDateTime getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public void setFechaHoraCreacion(LocalDateTime fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<DetallePedidoMesa> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoMesa> detalles) {
        this.detalles = detalles;
    }
}

