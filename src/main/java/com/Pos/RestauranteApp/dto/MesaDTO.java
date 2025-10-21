package com.Pos.RestauranteApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MesaDTO {

    private Long idMesa;

    @NotNull(message = "El número de mesa no puede ser nulo")
    private Integer numeroMesa;

    @NotNull(message = "La capacidad no puede ser nula")
    @Positive(message = "La capacidad debe ser un número positivo")
    private Integer capacidad;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
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
