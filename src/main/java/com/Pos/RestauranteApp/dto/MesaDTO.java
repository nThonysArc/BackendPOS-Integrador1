package com.Pos.RestauranteApp.dto;

public class MesaDTO {

    private Long idMesa;
    private Integer numeroMesa;
    private Integer capacidad;
    private String estado;

    // Constructor vacío
    public MesaDTO() {}

    // Constructor completo
    public MesaDTO(Long idMesa, Integer numeroMesa, Integer capacidad, String estado) {
        this.idMesa = idMesa;
        this.numeroMesa = numeroMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdMesa() { return idMesa; }
    public void setIdMesa(Long idMesa) { this.idMesa = idMesa; }

    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
