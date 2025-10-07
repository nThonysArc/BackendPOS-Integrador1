package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.RolDTO;
import com.Pos.RestauranteApp.service.RolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
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
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    @PostMapping
    public RolDTO crear(@RequestBody RolDTO rolDTO) {
        return rolService.guardar(rolDTO);
    }

    @PutMapping("/{id}")
    public RolDTO actualizar(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        rolDTO.setIdRol(id);
        return rolService.guardar(rolDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
    }
}
