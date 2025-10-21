package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.RolDTO;
import com.Pos.RestauranteApp.service.RolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<RolDTO> listarTodos() {
        return rolService.listarTodos();
    }

    @GetMapping("/{id}")
    public RolDTO obtenerPorId(@PathVariable Long id) {
        return rolService.buscarPorId(id)
                // TODO: Reemplazar con ResourceNotFoundException
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    @PostMapping
    public RolDTO crear(@RequestBody RolDTO rolDTO) {
        // TODO: Añadir validación al RolDTO
        return rolService.guardar(rolDTO);
    }

    @PutMapping("/{id}")
    public RolDTO actualizar(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        // TODO: Añadir validación al RolDTO
        rolDTO.setIdRol(id);
        return rolService.guardar(rolDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
    }
}