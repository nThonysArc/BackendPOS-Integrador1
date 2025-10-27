package com.Pos.RestauranteApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_pedidosmesa")
public class DetallePedidoMesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedidoMesa;

    @ManyToOne
    @JoinColumn(name = "id_pedidoMesa", nullable = false)
    private PedidoMesa pedidoMesa;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private Integer cantidad;

    private Double precioUnitario;

    // --- ¡¡NUEVO CAMPO AÑADIDO!! ---
    /**
     * Estado de este detalle específico.
     * PENDIENTE: Recién añadido por el mesero, listo para cocina.
     * LISTO: Cocina ya lo preparó.
     * ENTREGADO: (Opcional) Mesero lo entregó.
     */
    private String estadoDetalle;

    // Getters y setters

    public Long getIdDetallePedidoMesa() {
        return idDetallePedidoMesa;
    }

    public void setIdDetallePedidoMesa(Long idDetallePedidoMesa) {
        this.idDetallePedidoMesa = idDetallePedidoMesa;
    }

    public PedidoMesa getPedidoMesa() {
        return pedidoMesa;
    }

    public void setPedidoMesa(PedidoMesa pedidoMesa) {
        this.pedidoMesa = pedidoMesa;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getEstadoDetalle() {
        return estadoDetalle;
    }

    public void setEstadoDetalle(String estadoDetalle) {
        this.estadoDetalle = estadoDetalle;
    }
}