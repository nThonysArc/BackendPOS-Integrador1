package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.EmpleadoDTO;
import com.Pos.RestauranteApp.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmpleadoDTO> listarTodos() {
        return empleadoService.listarTodos();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customSecurityService.esElMismoUsuario(#id)")
    public EmpleadoDTO obtenerPorId(@PathVariable Long id) {
        return empleadoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmpleadoDTO crear(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        return empleadoService.guardar(empleadoDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customSecurityService.esElMismoUsuario(#id)")
    public EmpleadoDTO actualizar(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO empleadoDTO) {
        empleadoDTO.setIdEmpleado(id);
        return empleadoService.guardar(empleadoDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }

}
