package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.MesaDTO;
import com.Pos.RestauranteApp.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.Pos.RestauranteApp.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier frontend
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    // 🔹 Listar todas las mesas
    @GetMapping
    public ResponseEntity<List<MesaDTO>> listar() {
        return ResponseEntity.ok(mesaService.listar());
    }

    // 🔹 Obtener mesa por ID
    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> obtenerPorId(@PathVariable Long id) {
        MesaDTO mesa = mesaService.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
        return ResponseEntity.ok(mesa);
    }

    @PostMapping
    public ResponseEntity<MesaDTO> crear(@Valid @RequestBody MesaDTO mesaDTO) {
        return ResponseEntity.ok(mesaService.guardar(mesaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MesaDTO mesaDTO) {
        mesaDTO.setIdMesa(id);
        return ResponseEntity.ok(mesaService.guardar(mesaDTO));
    }

    // Eliminar mesa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar estado de mesa (por ejemplo, DISPONIBLE → OCUPADA)
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<MesaDTO> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado
    ) {
        return ResponseEntity.ok(mesaService.cambiarEstado(id, nuevoEstado));
    }
}
