package com.Pos.RestauranteApp.dto.WEB;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PedidoWebDTO {

    private Long idPedidoWeb; // Null al crear, lleno al responder

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idClienteWeb;
    
    private String nombreCliente; // Para mostrar en el POS fácilmente

    private LocalDateTime fechaHora; // Asignado por backend, pero útil en respuesta
    
    private String estado; // PENDIENTE, CONFIRMADO, etc.

    // --- Datos de Entrega (Formulario del Checkout) ---
    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    private String telefonoContacto;

    private String referencia; // Ej: "Frente al parque"

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // "EFECTIVO", "YAPE", "TARJETA"

    private Double total;

    // --- El Carrito de Compras ---
    @Valid // Valida cada objeto dentro de la lista
    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<DetallePedidoWebDTO> detalles;

    public PedidoWebDTO() {}

    // Getters y Setters
    public Long getIdPedidoWeb() { return idPedidoWeb; }
    public void setIdPedidoWeb(Long idPedidoWeb) { this.idPedidoWeb = idPedidoWeb; }

    public Long getIdClienteWeb() { return idClienteWeb; }
    public void setIdClienteWeb(Long idClienteWeb) { this.idClienteWeb = idClienteWeb; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<DetallePedidoWebDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoWebDTO> detalles) { this.detalles = detalles; }
}