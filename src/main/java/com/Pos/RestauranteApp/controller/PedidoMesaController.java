package com.Pos.RestauranteApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.service.PedidoMesaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidosMesa")
@CrossOrigin(origins = "*") 
public class PedidoMesaController {

    private final PedidoMesaService pedidoMesaService;

    public PedidoMesaController(PedidoMesaService pedidoMesaService) {
        this.pedidoMesaService = pedidoMesaService;
    }

    //  Listar todos los pedidos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO', 'COCINA')") 
    public ResponseEntity<List<PedidoMesaDTO>> listarPedidos() {
        List<PedidoMesaDTO> pedidos = pedidoMesaService.listar();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO', 'COCINA')") 
    public ResponseEntity<PedidoMesaDTO> obtenerPedido(@PathVariable Long id) {
        PedidoMesaDTO pedido = pedidoMesaService.obtenerPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/mesa/{mesaId}/activo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    public ResponseEntity<PedidoMesaDTO> obtenerPedidoActivoPorMesa(@PathVariable Long mesaId) {
        PedidoMesaDTO pedido = pedidoMesaService.obtenerPedidoActivoPorMesa(mesaId);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedido);
    }


    //  Crear nuevo pedido
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    public ResponseEntity<PedidoMesaDTO> crearPedido(@Valid @RequestBody PedidoMesaDTO pedidoDTO) {
        PedidoMesaDTO nuevoPedido = pedidoMesaService.guardar(pedidoDTO);
        return ResponseEntity.ok(nuevoPedido);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    public ResponseEntity<PedidoMesaDTO> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoMesaDTO pedidoDTO) {
        
        PedidoMesaDTO pedidoActualizado = pedidoMesaService.actualizar(id, pedidoDTO);
        return ResponseEntity.ok(pedidoActualizado);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN', 'MESERO')")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoMesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<PedidoMesaDTO> cerrarPedido(@PathVariable Long id) {
        PedidoMesaDTO pedidoCerrado = pedidoMesaService.cerrarPedido(id);
        return ResponseEntity.ok(pedidoCerrado);
    }
    @PutMapping("/{id}/estado/{nuevoEstado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'COCINA')") 
    public ResponseEntity<PedidoMesaDTO> cambiarEstadoPedido(
            @PathVariable Long id,
            @PathVariable String nuevoEstado) {
        
        PedidoMesaDTO pedidoActualizado = pedidoMesaService.cambiarEstadoPedido(id, nuevoEstado);
        return ResponseEntity.ok(pedidoActualizado);
    }
    
    @PutMapping("/{id}/marcarListos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COCINA')")
    public ResponseEntity<PedidoMesaDTO> marcarListos(@PathVariable Long id) {
        PedidoMesaDTO pedidoActualizado = pedidoMesaService.marcarPendientesComoListos(id);
        return ResponseEntity.ok(pedidoActualizado);
    }
}