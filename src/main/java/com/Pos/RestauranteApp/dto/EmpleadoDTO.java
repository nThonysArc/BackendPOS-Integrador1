package com.Pos.RestauranteApp.dto;

public class EmpleadoDTO {
    private Long idEmpleado;
    private String nombre;
    private String dni;
    private String usuario;
    private String rolNombre; // en lugar de devolver todo el objeto Rol

    public EmpleadoDTO(Long idEmpleado, String nombre, String dni, String usuario, String rolNombre) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.dni = dni;
        this.usuario = usuario;
        this.rolNombre = rolNombre;
    }
// getters y setters

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}

