package com.Pos.RestauranteApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.Pos.RestauranteApp.dto.MesaDTO;
import com.Pos.RestauranteApp.service.MesaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MesaDTO>> listar() {
        // CORRECCIÓN: Usar el nuevo nombre 'getAllMesas'
        return ResponseEntity.ok(mesaService.getAllMesas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MesaDTO> obtenerPorId(@PathVariable Long id) {
        // CORRECCIÓN: El servicio ya lanza la excepción si no existe, no hace falta .orElseThrow aquí
        return ResponseEntity.ok(mesaService.getMesaById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> crear(@Valid @RequestBody MesaDTO mesaDTO) {
        // CORRECCIÓN: Usar 'crearMesa'
        return ResponseEntity.ok(mesaService.crearMesa(mesaDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MesaDTO mesaDTO) {
        // CORRECCIÓN: Usar 'actualizarMesa' y pasar ID explícitamente
        return ResponseEntity.ok(mesaService.actualizarMesa(id, mesaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // CORRECCIÓN: Usar 'eliminarMesa'
        mesaService.eliminarMesa(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado/{nuevoEstado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado
    ) {
        // CORRECCIÓN: Aseguramos que este método exista en el servicio (ver abajo)
        return ResponseEntity.ok(mesaService.cambiarEstado(id, nuevoEstado));
    }
}