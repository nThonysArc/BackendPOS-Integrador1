package com.Pos.RestauranteApp.model.WEB;

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
@Table(name = "pedidos_web")
public class PedidoWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedidoWeb;

    @ManyToOne
    @JoinColumn(name = "id_cliente_web", nullable = false)
    private ClienteWeb clienteWeb;

    private LocalDateTime fechaHoraPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EstadoPedidoWeb estado = EstadoPedidoWeb.PENDIENTE;

    private Double total = 0.0;

    // Datos específicos de entrega
    private String direccionEntrega;
    private String telefonoContacto;
    private String referencia; 
    private String metodoPago; 

    @OneToMany(mappedBy = "pedidoWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedidoWeb> detalles;

    public enum EstadoPedidoWeb {
        PENDIENTE,
        EN_COCINA,
        LISTO,     
        EN_CAMINO,  
        ENTREGADO,
        CANCELADO      
    }

    // Getters y Setters
    public Long getIdPedidoWeb() { return idPedidoWeb; }
    public void setIdPedidoWeb(Long idPedidoWeb) { this.idPedidoWeb = idPedidoWeb; }

    public ClienteWeb getClienteWeb() { return clienteWeb; }
    public void setClienteWeb(ClienteWeb clienteWeb) { this.clienteWeb = clienteWeb; }

    public LocalDateTime getFechaHoraPedido() { return fechaHoraPedido; }
    public void setFechaHoraPedido(LocalDateTime fechaHoraPedido) { this.fechaHoraPedido = fechaHoraPedido; }

    public EstadoPedidoWeb getEstado() { return estado; }
    public void setEstado(EstadoPedidoWeb estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public List<DetallePedidoWeb> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoWeb> detalles) { this.detalles = detalles; }
}