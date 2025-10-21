package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.MesaDTO;
import com.Pos.RestauranteApp.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    // 🔹 Listar todas las mesas (Cualquiera autenticado)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MesaDTO>> listar() {
        return ResponseEntity.ok(mesaService.listar());
    }

    // 🔹 Obtener mesa por ID (Cualquiera autenticado)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MesaDTO> obtenerPorId(@PathVariable Long id) {
        MesaDTO mesa = mesaService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        return ResponseEntity.ok(mesa);
    }

    // 🔹 Crear nueva mesa (Solo Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> crear(@Valid @RequestBody MesaDTO mesaDTO) {
        return ResponseEntity.ok(mesaService.guardar(mesaDTO));
    }

    // 🔹 Actualizar mesa (Solo Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MesaDTO mesaDTO) {
        mesaDTO.setIdMesa(id);
        return ResponseEntity.ok(mesaService.guardar(mesaDTO));
    }

    // 🔹 Eliminar mesa (Solo Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Cambiar estado de mesa (Solo Admin)
    // (Un MESERO no debería cambiar el estado directamente,
    // esto se maneja automáticamente al crear/cerrar un pedido)
    @PutMapping("/{id}/estado/{nuevoEstado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MesaDTO> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado
    ) {
        return ResponseEntity.ok(mesaService.cambiarEstado(id, nuevoEstado));
    }
}