package com.Pos.RestauranteApp.controller;

import com.Pos.RestauranteApp.dto.PedidoMesaDTO;
import com.Pos.RestauranteApp.service.PedidoMesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/pedidosMesa")
@CrossOrigin(origins = "*") // permite consumir desde el frontend
public class PedidoMesaController {

    private final PedidoMesaService pedidoMesaService;

    public PedidoMesaController(PedidoMesaService pedidoMesaService) {
        this.pedidoMesaService = pedidoMesaService;
    }

    //  Listar todos los pedidos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<List<PedidoMesaDTO>> listarPedidos() {
        List<PedidoMesaDTO> pedidos = pedidoMesaService.listar();
        return ResponseEntity.ok(pedidos);
    }

    // Solo Meseros o Admins pueden ver un pedido
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<PedidoMesaDTO> obtenerPedido(@PathVariable Long id) {
        PedidoMesaDTO pedido = pedidoMesaService.obtenerPorId(id);
        return ResponseEntity.ok(pedido);
    }

    //  Crear nuevo pedido
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    public ResponseEntity<PedidoMesaDTO> crearPedido(@Valid @RequestBody PedidoMesaDTO pedidoDTO) {
        PedidoMesaDTO nuevoPedido = pedidoMesaService.guardar(pedidoDTO);
        return ResponseEntity.ok(nuevoPedido);
    }

    //  Eliminar pedido (también libera la mesa)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN', 'MESERO')")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoMesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //  Cerrar pedido (sin eliminarlo)
    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<PedidoMesaDTO> cerrarPedido(@PathVariable Long id) {
        PedidoMesaDTO pedidoCerrado = pedidoMesaService.cerrarPedido(id);
        return ResponseEntity.ok(pedidoCerrado);
    }
}
