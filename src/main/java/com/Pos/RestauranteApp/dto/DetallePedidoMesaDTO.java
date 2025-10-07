package com.Pos.RestauranteApp.dto;

public class DetallePedidoMesaDTO {

    private Long idDetallePedidoMesa;
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public DetallePedidoMesaDTO() {}

    public DetallePedidoMesaDTO(Long idDetallePedidoMesa, Long idProducto, String nombreProducto,
                                Integer cantidad, Double precioUnitario) {
        this.idDetallePedidoMesa = idDetallePedidoMesa;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
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
}

