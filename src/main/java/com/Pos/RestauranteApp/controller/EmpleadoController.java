package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.model.Empleado;
import com.Pos.RestauranteApp.service.EmpleadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<Empleado> listarTodos() {
        return empleadoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Empleado obtenerPorId(@PathVariable Long id) {
        return empleadoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @PostMapping
    public Empleado crear(@RequestBody Empleado empleado) {
        return empleadoService.guardar(empleado);
    }

    @PutMapping("/{id}")
    public Empleado actualizar(@PathVariable Long id, @RequestBody Empleado empleado) {
        Empleado existente = empleadoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        existente.setNombre(empleado.getNombre());
        existente.setDni(empleado.getDni());
        existente.setUsuario(empleado.getUsuario());
        existente.setContrasena(empleado.getContrasena());
        existente.setRol(empleado.getRol());
        return empleadoService.guardar(existente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }
}

