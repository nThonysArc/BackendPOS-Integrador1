package com.Pos.RestauranteApp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DetallePedidoMesaDTO {

    private Long idDetallePedidoMesa;

    @NotNull(message = "El idProducto no puede ser nulo en un detalle")
    private Long idProducto;

    private String nombreProducto;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @Positive(message = "El precio unitario debe ser positivo")
    private Double precioUnitario;

    private Double subtotal;

    // --- ¡¡NUEVO CAMPO AÑADIDO!! ---
    private String estadoDetalle;

    public DetallePedidoMesaDTO() {}

    public DetallePedidoMesaDTO(Long idDetallePedidoMesa, Long idProducto, String nombreProducto,
                                Integer cantidad, Double precioUnitario, String estadoDetalle) { // <-- Modificado
        this.idDetallePedidoMesa = idDetallePedidoMesa;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
        this.estadoDetalle = estadoDetalle; // <-- Añadido
    }

    // Getters y setters
    public Long getIdDetallePedidoMesa() { return idDetallePedidoMesa; }
    public void setIdDetallePedidoMesa(Long idDetallePedidoMesa) { this.idDetallePedidoMesa = idDetallePedidoMesa; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    // --- NUEVO GETTER/SETTER ---
    public String getEstadoDetalle() { return estadoDetalle; }
    public void setEstadoDetalle(String estadoDetalle) { this.estadoDetalle = estadoDetalle; }
}