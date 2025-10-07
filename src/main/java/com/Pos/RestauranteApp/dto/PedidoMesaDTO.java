package com.Pos.RestauranteApp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoMesaDTO {

    private Long idPedidoMesa;
    private Long idMesa;
    private Integer numeroMesa;
    private Long idMesero;
    private String nombreMesero;
    private LocalDateTime fechaHoraCreacion;
    private String estado;
    private Double total;
    private List<DetallePedidoMesaDTO> detalles;

    public PedidoMesaDTO() {}

    public PedidoMesaDTO(Long idPedidoMesa, Long idMesa, Integer numeroMesa,
                         Long idMesero, String nombreMesero,
                         LocalDateTime fechaHoraCreacion, String estado,
                         Double total, List<DetallePedidoMesaDTO> detalles) {
        this.idPedidoMesa = idPedidoMesa;
        this.idMesa = idMesa;
        this.numeroMesa = numeroMesa;
        this.idMesero = idMesero;
        this.nombreMesero = nombreMesero;
        this.fechaHoraCreacion = fechaHoraCreacion;
        this.estado = estado;
        this.total = total;
        this.detalles = detalles;
    }

    // Getters y setters
    public Long getIdPedidoMesa() { return idPedidoMesa; }
    public void setIdPedidoMesa(Long idPedidoMesa) { this.idPedidoMesa = idPedidoMesa; }

    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }

    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }

    public Long getIdMesero() { return idMesero; }
    public void setIdMesero(Long idMesero) { this.idMesero = idMesero; }

    public String getNombreMesero() { return nombreMesero; }
    public void setNombreMesero(String nombreMesero) { this.nombreMesero = nombreMesero; }

    public LocalDateTime getFechaHoraCreacion() { return fechaHoraCreacion; }
    public void setFechaHoraCreacion(LocalDateTime fechaHoraCreacion) { this.fechaHoraCreacion = fechaHoraCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<DetallePedidoMesaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoMesaDTO> detalles) { this.detalles = detalles; }
}
