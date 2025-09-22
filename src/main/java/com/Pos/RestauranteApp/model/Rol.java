package com.Pos.RestauranteApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    // Relación con empleados
    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    private List<Empleado> empleados;

    public Rol() {}

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Empleado> getEmpleados() { return empleados; }
    public void setEmpleados(List<Empleado> empleados) { this.empleados = empleados; }
}

