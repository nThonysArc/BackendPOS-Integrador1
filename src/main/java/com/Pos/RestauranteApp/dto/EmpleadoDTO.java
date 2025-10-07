package com.Pos.RestauranteApp.dto;

public class EmpleadoDTO {
    private Long idEmpleado;
    private String nombre;
    private String dni;
    private String usuario;
    private String rolNombre;
    private Long idRol;

    public EmpleadoDTO() {}

    public EmpleadoDTO(Long idEmpleado, String nombre, String dni, String usuario, String rolNombre, Long idRol) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.dni = dni;
        this.usuario = usuario;
        this.rolNombre = rolNombre;
        this.idRol = idRol;
    }

    // Getters y Setters
    public Long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Long idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }
}
