package com.Pos.RestauranteApp.dto.WEB;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DetallePedidoWebDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    private String nombreProducto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private Double precioUnitario; 

    private String observaciones; 

    public DetallePedidoWebDTO() {}

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}