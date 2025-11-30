package com.Pos.RestauranteApp.model.WEB;

import com.Pos.RestauranteApp.model.Producto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_pedidos_web")
public class DetallePedidoWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleWeb;

    @ManyToOne
    @JoinColumn(name = "id_pedido_web", nullable = false)
    private PedidoWeb pedidoWeb;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    
    private String observaciones; 

    // Getters y Setters
    public Long getIdDetalleWeb() { return idDetalleWeb; }
    public void setIdDetalleWeb(Long idDetalleWeb) { this.idDetalleWeb = idDetalleWeb; }

    public PedidoWeb getPedidoWeb() { return pedidoWeb; }
    public void setPedidoWeb(PedidoWeb pedidoWeb) { this.pedidoWeb = pedidoWeb; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}